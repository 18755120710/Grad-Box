<template>
  <div class="max-w-4xl mx-auto px-4 py-20 space-y-12">
    <header class="text-center space-y-4">
      <h1 class="text-5xl font-black text-slate-900">帮助中心</h1>
      <p class="text-xl text-slate-500">解答您的所有疑惑</p>
    </header>

    <div class="grid grid-cols-1 gap-6">
      <div v-for="(item, i) in faqs" :key="i" class="bg-white p-8 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md transition-shadow">
        <h4 class="text-lg font-black text-slate-800 mb-2 flex items-center gap-3">
          <span class="w-8 h-8 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center text-sm">Q</span>
          {{ item.q }}
        </h4>
        <p class="text-slate-600 pl-11">{{ item.a }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const faqs = [
  { q: '如何购买项目参考包？', a: '在项目详情页点击“立即购买”，支付完成后即可在个人中心查看下载地址或联系客服获取。' },
  { q: '购买后可以提供环境搭建指导吗？', a: '可以。我们提供远程调试与环境搭建增值服务，详情请点击“定制服务”咨询。' },
  { q: '如果代码运行报错怎么办？', a: '我们保证源码在标准环境下可运行。如果遇到问题，请第一时间联系客服，我们提供 7 天技术支持。' },
  { q: '可以定制全新的毕业设计吗？', a: '当然可以。请通过“定制服务”页签提交您的需求，我们的导师会与您联系沟通细节。' }
]
</script>
