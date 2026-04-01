// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-04-03',
  devtools: { enabled: true },
  modules: [
    '@nuxtjs/tailwindcss',
    '@pinia/nuxt',
    '@element-plus/nuxt'
  ],
  css: ['~/assets/css/tailwind.css'],
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  app: {
    head: {
      title: 'GradBox | 毕业设计展示与服务平台',
      link: [
        { rel: 'icon', type: 'image/png', href: '/logo.png' }
      ]
    },
    pageTransition: { name: 'page', mode: 'out-in' }
  }
})
