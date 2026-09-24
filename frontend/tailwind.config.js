export default {
  content: ['./index.html', './src/**/*.{vue,js}'],
  theme: {
    extend: {
      colors: {
        workshop: {
          ink: '#17202a',
          steel: '#2f4858',
          blue: '#1d72b8',
          teal: '#00a896',
          amber: '#f59e0b',
          smoke: '#f4f7fb'
        }
      },
      boxShadow: {
        soft: '0 18px 60px rgba(23, 32, 42, 0.10)'
      }
    }
  },
  plugins: []
}
