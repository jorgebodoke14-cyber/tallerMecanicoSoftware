<template>
  <section class="mx-auto grid min-h-[70vh] max-w-xl place-items-center px-4 py-8">
    <div class="panel w-full p-8 text-center">
      <h1 class="text-2xl font-black text-workshop-ink">Interfaz en construccion</h1>
      <p class="mt-3 text-sm leading-6 text-slate-600">Tu rol esta autenticado. Proximamente tendra una interfaz de trabajo asignada.</p>
      <button class="btn-secondary mt-6" type="button" @click="$emit('logout')">Cerrar sesion</button>
    </div>
  </section>
</template>

<script setup>
defineEmits(['logout'])
</script>
