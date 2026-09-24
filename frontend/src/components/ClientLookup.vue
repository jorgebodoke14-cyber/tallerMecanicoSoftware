<template>
  <section class="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">
    <div class="grid gap-6 lg:grid-cols-[360px_1fr]">
      <form class="panel p-6" @submit.prevent="lookup">
        <h2 class="text-2xl font-bold">Consulta de cliente</h2>
        <p class="mt-2 text-sm text-slate-500">Ingresa el folio de recepcion para revisar el avance del vehiculo.</p>
        <label class="label mt-6 block">Folio</label>
        <input v-model="folio" class="field mt-1" placeholder="REC-2026-0142" />
        <button class="btn-primary mt-4 w-full" type="submit">
          <Search class="h-4 w-4" />
          Consultar
        </button>
      </form>

      <div class="panel p-6">
        <div class="flex flex-col gap-3 border-b border-slate-200 pb-5 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p class="text-sm font-semibold text-workshop-blue">{{ order.folio }}</p>
            <h2 class="text-2xl font-bold">{{ order.vehicle }}</h2>
          </div>
          <span class="w-fit rounded-md bg-amber-100 px-3 py-1 text-sm font-semibold text-amber-800">{{ order.status }}</span>
        </div>

        <div class="mt-6 grid gap-4 sm:grid-cols-3">
          <div v-for="step in order.steps" :key="step.name" class="rounded-md border border-slate-200 p-4">
            <component :is="step.icon" class="mb-3 h-5 w-5 text-workshop-teal" />
            <p class="font-semibold">{{ step.name }}</p>
            <p class="text-sm text-slate-500">{{ step.detail }}</p>
          </div>
        </div>

        <div class="mt-6">
          <div class="mb-2 flex justify-between text-sm font-semibold">
            <span>Avance general</span>
            <span>{{ order.progress }}%</span>
          </div>
          <div class="h-3 rounded-full bg-slate-100">
            <div class="h-3 rounded-full bg-workshop-teal" :style="{ width: `${order.progress}%` }"></div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ClipboardCheck, Search, Settings, Sparkles } from 'lucide-vue-next'
import { apiRequest } from '../services/api'

const folio = ref('REC-2026-0142')
const order = reactive({
  folio: 'REC-2026-0142',
  vehicle: 'Nissan Versa 2020',
  status: 'En diagnostico',
  progress: 45,
  steps: [
    { name: 'Recepcion', detail: 'Unidad recibida y documentada.', icon: ClipboardCheck },
    { name: 'Diagnostico', detail: 'Mecanico asignado revisando falla.', icon: Settings },
    { name: 'Entrega', detail: 'Pendiente de autorizacion.', icon: Sparkles }
  ]
})

async function lookup() {
  try {
    const data = await apiRequest(`/public/orders/${folio.value}`)
    Object.assign(order, data)
  } catch {
    order.folio = folio.value || 'REC-2026-0142'
  }
}
</script>
