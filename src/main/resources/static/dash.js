function toggleDarkMode() {
      const body = document.body;
      body.classList.toggle('dark-mode');

      // Toggle footer dark mode
      const footer = document.querySelector('.site-footer');
      if(footer) footer.classList.toggle('dark-mode');

      // Store preference
      const isDark = body.classList.contains('dark-mode');
      localStorage.setItem('darkMode', isDark);

      // Update toggle button icon
      const icon = document.querySelector('.dark-mode-toggle i');
      icon.className = isDark ? 'fas fa-sun' : 'fas fa-moon';
  }
  // Initialize dark mode from localStorage
  document.addEventListener('DOMContentLoaded', () => {
      const isDark = localStorage.getItem('darkMode') === 'true';
      if(isDark) {
          document.body.classList.add('dark-mode');
          document.querySelector('.dark-mode-toggle i').className = 'fas fa-sun';
      }
  });
  // Add smooth scroll behavior
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
      anchor.addEventListener('click', function (e) {
          e.preventDefault();
          document.querySelector(this.getAttribute('href')).scrollIntoView({
              behavior: 'smooth'
          });
      });
  });