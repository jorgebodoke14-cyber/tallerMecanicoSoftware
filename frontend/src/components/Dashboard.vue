<template>
  <section class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
    <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase tracking-wide text-workshop-blue">{{ roleLabel }}</p>
        <h1 class="text-3xl font-black">Hola, {{ user.name }}</h1>
        <p class="mt-2 text-slate-500">Vista operativa con ordenes, empleados, clientes y auditoria de actividad.</p>
      </div>
      <button class="btn-primary" @click="$emit('create-order')">
        <Plus class="h-4 w-4" />
        Nueva recepcion
      </button>
    </div>

    <div class="grid gap-4 md:grid-cols-4">
      <div v-for="metric in metrics" :key="metric.label" class="panel p-5">
        <component :is="metric.icon" class="mb-4 h-5 w-5 text-workshop-blue" />
        <p class="text-3xl font-black">{{ metric.value }}</p>
        <p class="text-sm text-slate-500">{{ metric.label }}</p>
      </div>
    </div>

    <div class="mt-6 grid gap-6 lg:grid-cols-[1.4fr_0.8fr]">
      <div class="panel overflow-hidden">
        <div class="border-b border-slate-200 p-5">
          <h2 class="text-xl font-bold">Ordenes activas</h2>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-slate-200 text-sm">
            <thead class="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th class="px-5 py-3">Folio</th>
                <th class="px-5 py-3">Cliente</th>
                <th class="px-5 py-3">Vehiculo</th>
                <th class="px-5 py-3">Estado</th>
                <th class="px-5 py-3">Mecanico</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100 bg-white">
              <tr v-for="order in orders" :key="order.folio">
                <td class="px-5 py-4 font-semibold text-workshop-blue">{{ order.folio }}</td>
                <td class="px-5 py-4">{{ order.client }}</td>
                <td class="px-5 py-4">{{ order.vehicle }}</td>
                <td class="px-5 py-4">
                  <span class="rounded-md bg-teal-50 px-2.5 py-1 text-xs font-semibold text-teal-700">{{ order.status }}</span>
                </td>
                <td class="px-5 py-4">{{ order.mechanic }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="panel p-5">
        <h2 class="text-xl font-bold">Auditoria reciente</h2>
        <div class="mt-4 space-y-4">
          <div v-for="log in auditLogs" :key="log.id" class="border-l-4 border-workshop-teal pl-4">
            <p class="font-semibold">{{ log.action }}</p>
            <p class="text-sm text-slate-500">{{ log.actor }} · {{ log.time }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { Banknote, ClipboardList, Plus, ShieldCheck, Users } from 'lucide-vue-next'

const props = defineProps({
  user: { type: Object, required: true }
})

defineEmits(['create-order'])

const roleNames = {
  OWNER: 'Dueno',
  MANAGER: 'Gerente',
  SECRETARY: 'Secretaria',
  MECHANIC: 'Mecanico',
  ACCOUNTANT: 'Contador'
}

const roleLabel = computed(() => roleNames[props.user.role] || props.user.role)

const metrics = [
  { label: 'ordenes abiertas', value: '18', icon: ClipboardList },
  { label: 'empleados activos', value: '11', icon: Users },
  { label: 'pagos pendientes', value: '$42k', icon: Banknote },
  { label: 'eventos auditados hoy', value: '136', icon: ShieldCheck }
]

const orders = reactive([
  { folio: 'REC-2026-0142', client: 'Mariana Perez', vehicle: 'Nissan Versa 2020', status: 'Diagnostico', mechanic: 'Luis M.' },
  { folio: 'REC-2026-0143', client: 'Carlos Ruiz', vehicle: 'VW Jetta 2018', status: 'Refacciones', mechanic: 'Brenda S.' },
  { folio: 'REC-2026-0144', client: 'Sofia Diaz', vehicle: 'Toyota Corolla 2021', status: 'Prueba', mechanic: 'Ivan G.' }
])

const auditLogs = reactive([
  { id: 1, action: 'Login exitoso', actor: props.user.email, time: 'hace 2 min' },
  { id: 2, action: 'Orden REC-2026-0142 consultada', actor: 'dueno@taller.com', time: 'hace 8 min' },
  { id: 3, action: 'Cambio de estado a Diagnostico', actor: 'secretaria@taller.com', time: 'hace 16 min' }
])

onMounted(() => {
  window.addEventListener('demo-order-created', () => {
    orders.unshift({
      folio: `REC-2026-${String(145 + orders.length).padStart(4, '0')}`,
      client: 'Cliente nuevo',
      vehicle: 'Vehiculo por capturar',
      status: 'Recepcion',
      mechanic: 'Sin asignar'
    })
  })
})
</script>
