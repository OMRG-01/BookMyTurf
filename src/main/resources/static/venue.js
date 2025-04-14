
let grounds = []; // Store all grounds initially
   let activeFilters = {
     name: '',
     city: '',
     game: ''
   };

   $(document).ready(function() {
     // Initialize
     showLoader();
     fetchGrounds();
     
     // Real-time search functionality
     $('#searchName').on('input', debounce(function() {
       activeFilters.name = $(this).val().trim().toLowerCase();
       updateActiveFiltersDisplay();
       applyFilters();
     }, 300));
     
     $('#searchCity').on('input', debounce(function() {
       activeFilters.city = $(this).val().trim().toLowerCase();
       updateActiveFiltersDisplay();
       applyFilters();
     }, 300));
     
     $('#searchGame').on('change', function() {
       activeFilters.game = $(this).val();
       updateActiveFiltersDisplay();
       applyFilters();
     });
     
     // Search button click (for mobile users)
     $('#searchBtn').on('click', function() {
       activeFilters.name = $('#searchName').val().trim().toLowerCase();
       activeFilters.city = $('#searchCity').val().trim().toLowerCase();
       activeFilters.game = $('#searchGame').val();
       
       updateActiveFiltersDisplay();
       applyFilters();
     });
     
     // Reset filters button
     $('#resetFilters').on('click', resetFilters);
     
     // Close modal
     $('#closeModal').on('click', function() {
       closeLoginModal();
     });
     
     // Close modal if clicked outside
     $(window).on('click', function(e) {
       if ($(e.target).hasClass('modal-overlay')) {
         closeLoginModal();
       }
     });
   });

   function fetchGrounds() {
     $.get("/getAllGrounds", function(data) {
       grounds = data;
       populateGamesFilter(data);
	setGameFromQueryParam();
       displayGrounds(data);
       hideLoader();
       updateSearchStatus(data.length);
     }).fail(function() {
       // Handle error
       hideLoader();
       $('#groundContainer').html(`
         <div class="empty-state">
           <i class="fas fa-exclamation-circle"></i>
           <h3>Oops! Something went wrong</h3>
           <p>We couldn't load the venues. Please try again later.</p>
           <button onclick="fetchGrounds()" class="search-btn">Try Again</button>
         </div>
       `);
     });
   }

   function populateGamesFilter(data) {
     let gameSet = new Set();
     data.forEach(g => gameSet.add(g.game.gameName));
     
     // Sort games alphabetically
     let sortedGames = Array.from(gameSet).sort();
     
     sortedGames.forEach(game => {
       $('#searchGame').append(`<option value="${game}">${game}</option>`);
     });
   }

   function displayGrounds(groundsList) {
     let container = $('#groundContainer');
     container.empty();
     
     if (groundsList.length === 0) {
       $('#emptyState').show();
       return;
     }
     
     $('#emptyState').hide();
     
     groundsList.forEach(g => {
       let floodlightsIcon = g.floodlightsAvailable ? 
         '<i class="fas fa-lightbulb yes"></i> Yes' : 
         '<i class="fas fa-lightbulb-slash no"></i> No';
         
       let changingRoomsIcon = g.changingRooms ? 
         '<i class="fas fa-tshirt yes"></i> Yes' : 
         '<i class="fas fa-tshirt no"></i> No';

       let card = `
         <div class="ground-card">
           <div class="card-img-container">
             <img src="/images/${g.imageFileName}" alt="${g.name}">
             <div class="sport-badge">${g.game.gameName}</div>
           </div>
           <div class="card-content">
             <h3 class="ground-name">${g.name}</h3>
             <div class="ground-location">
               <i class="fas fa-map-marker-alt"></i> ${g.location}
             </div>
		  <div class="ground-location">
		  					<span class="map-link" onclick="openGoogleMaps('${g.address}')" ><i class="fas fa-map-marker-alt"></i> ${g.address}</span>
		  				</div>
             <div class="ground-details">
               <div class="detail-item tooltip">
                 <i class="far fa-clock"></i> ${g.openingTime}
                 <span class="tooltip-text">Opening Time</span>
               </div>
               <div class="detail-item tooltip">
                 <i class="fas fa-clock"></i> ${g.closingTime}
                 <span class="tooltip-text">Closing Time</span>
               </div>
             </div>
             
             <div class="amenities">
               <div class="amenity">
                 ${floodlightsIcon}
               </div>
               <div class="amenity">
                 ${changingRoomsIcon}
               </div>
             </div>
             
             <button class="view-btn" onclick="showLoginModal('${g.id}', '${g.name}')">
               View Details
             </button>
           </div>
         </div>
       `;
       container.append(card);
     });
   }

   function applyFilters() {
     showLoader();
     
     let filteredGrounds = grounds.filter(g =>
       (activeFilters.name === "" || g.name.toLowerCase().includes(activeFilters.name)) &&
       (activeFilters.city === "" || g.location.toLowerCase().includes(activeFilters.city)) &&
       (activeFilters.game === "" || g.game.gameName === activeFilters.game)
     );
     
     setTimeout(() => {
       displayGrounds(filteredGrounds);
       hideLoader();
       updateSearchStatus(filteredGrounds.length);
     }, 300); // Small timeout for better UX
   }
   
   function updateActiveFiltersDisplay() {
     let container = $('#activeFilters');
     container.empty();
     
     if (activeFilters.gameName) {
       container.append(`
         <div class="filter-tag">
           <span>Name: ${activeFilters.name}</span>
           <span class="remove-filter" onclick="removeFilter('name')">×</span>
         </div>
       `);
     }
     
     if (activeFilters.city) {
       container.append(`
         <div class="filter-tag">
           <span>City: ${activeFilters.city}</span>
           <span class="remove-filter" onclick="removeFilter('city')">×</span>
         </div>
       `);
     }
     
     if (activeFilters.game) {
       container.append(`
         <div class="filter-tag">
           <span>Sport: ${activeFilters.game}</span>
           <span class="remove-filter" onclick="removeFilter('game')">×</span>
         </div>
       `);
     }
   }
   
   function removeFilter(type) {
     activeFilters[type] = '';
     
     // Update input fields
     if (type === 'name') $('#searchName').val('');
     if (type === 'city') $('#searchCity').val('');
     if (type === 'game') $('#searchGame').val('');
     
     updateActiveFiltersDisplay();
     applyFilters();
   }
   
   function resetFilters() {
     activeFilters.name = '';
     activeFilters.city = '';
     activeFilters.game = '';
     
     $('#searchName').val('');
     $('#searchCity').val('');
     $('#searchGame').val('');
     
     updateActiveFiltersDisplay();
     applyFilters();
   }
   
   function updateSearchStatus(count) {
     if (
       activeFilters.name === '' && 
       activeFilters.city === '' && 
       activeFilters.game === ''
     ) {
       $('#searchStatus').text(`Showing all ${count} venues`);
     } else {
       $('#searchStatus').text(`Found ${count} venues matching your search`);
     }
   }
   
   function showLoader() {
     $('#loaderContainer').show();
     $('#groundContainer').hide();
   }
   
   function hideLoader() {
     $('#loaderContainer').hide();
     $('#groundContainer').show();
   }
   
   // Login modal functions
   function showLoginModal(groundId, groundName) {
     // Store the ground ID for later use
     sessionStorage.setItem('selectedGroundId', groundId);
     
     // Update venue name in the modal
     $('#venueName').text(groundName);
     
     // Show the modal
     $('#loginModal').addClass('active');
     
     // Prevent scrolling on the body
     $('body').css('overflow', 'hidden');
   }
   
   function closeLoginModal() {
     $('#loginModal').removeClass('active');
     $('body').css('overflow', 'auto');
   }
   
   // Debounce function to improve search performance
   function debounce(func, wait) {
     let timeout;
     return function() {
       const context = this;
       const args = arguments;
       clearTimeout(timeout);
       timeout = setTimeout(() => {
         func.apply(context, args);
       }, wait);
     };
   }
function openGoogleMaps(address) {
			    if (!address) {
			        alert("Address not available");
			        return;
			    }
			    
			    // Encode address for URL
			    let encodedAddress = encodeURIComponent(address);

			    // Open Google Maps in a new tab
			    window.open(`https://www.google.com/maps/search/?api=1&query=${encodedAddress}`, '_blank');
			}
			function setGameFromQueryParam() {
			  const params = new URLSearchParams(window.location.search);
			  const game = params.get("game");
			  
			  if (game) {
			    $('#searchGame').val(game);
			    activeFilters.game = game;
			    updateActiveFiltersDisplay();
			    applyFilters();
			  }
			}
