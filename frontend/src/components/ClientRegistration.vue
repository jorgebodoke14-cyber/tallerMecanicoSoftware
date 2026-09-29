<template>
  <section class="mx-auto w-full max-w-5xl px-4 py-8 sm:px-6">
    <header class="mb-6 flex flex-wrap items-center justify-between gap-4">
      <div>
        <p class="text-sm font-bold text-workshop-teal">Sucursal {{ sessionUser.branchId }}</p>
        <h1 class="text-3xl font-black text-workshop-ink">Registro de clientes</h1>
      </div>
      <button class="btn-secondary" type="button" @click="openClientsDialog">Ver clientes guardados</button>
    </header>
    <div v-if="!canRegister" class="panel p-6 text-sm text-red-700">Solo Administrador del sistema o Recepcionista puede registrar clientes.</div>
    <form v-else class="panel grid gap-6 p-6 sm:p-8" @submit.prevent="submit">
      <div class="grid gap-4 md:grid-cols-2">
        <label class="label">Nombre completo<input v-model.trim="form.fullName" class="field mt-1" required minlength="2" maxlength="120" /></label>
        <label class="label">Contacto alternativo<input v-model.trim="form.alternateContactName" class="field mt-1" required minlength="2" maxlength="120" /></label>
        <label class="label">Edad<input v-model.number="form.age" class="field mt-1" type="number" required min="18" max="120" /></label>
        <label class="label">Fecha de nacimiento<input v-model="form.birthDate" class="field mt-1" type="date" required :max="today" /></label>
        <label class="label">Telefono personal<input v-model.trim="form.personalPhone" class="field mt-1" required inputmode="numeric" pattern="[0-9]{10}" placeholder="10 digitos" /></label>
        <label class="label">Telefono de trabajo<input v-model.trim="form.workPhone" class="field mt-1" inputmode="numeric" pattern="[0-9]{10}" placeholder="Opcional, 10 digitos" /></label>
        <label class="label">Email<input v-model.trim="form.email" class="field mt-1" required type="email" maxlength="160" /></label>
        <label class="label">Email de trabajo<input v-model.trim="form.workEmail" class="field mt-1" type="email" maxlength="160" /></label>
      </div>
      <div class="border-t border-slate-200 pt-6"><h2 class="text-lg font-black">Direccion</h2>
        <div class="mt-4 grid gap-4 md:grid-cols-2">
          <label class="label">Calle<input v-model.trim="form.street" class="field mt-1" required /></label>
          <label class="label">Colonia<input v-model.trim="form.neighborhood" class="field mt-1" required /></label>
          <label class="label">Municipio<input v-model.trim="form.municipality" class="field mt-1" required /></label>
          <label class="label">Estado<input v-model.trim="form.state" class="field mt-1" required /></label>
          <label class="label">Codigo postal<input v-model.trim="form.postalCode" class="field mt-1" required inputmode="numeric" pattern="[0-9]{5}" maxlength="5" /></label>
        </div>
      </div>
      <div class="border-t border-slate-200 pt-6"><h2 class="text-lg font-black">Fotografia</h2>
        <div class="mt-3 flex flex-wrap items-start gap-5"><img v-if="previewUrl" :src="previewUrl" alt="Previsualizacion del cliente" class="h-24 w-24 rounded-md object-cover" />
          <div><input class="block text-sm" type="file" accept="image/jpeg,image/png,image/webp,.jpg,.jpeg,.png,.webp" @change="onPhoto" required />
          <p class="mt-2 text-xs text-slate-500">JPG, JPEG, PNG o WEBP; maximo 15 MB.</p></div></div>
      </div>
      <p v-if="error" class="rounded-md bg-red-50 p-3 text-sm text-red-700">{{ error }}</p>
      <p v-if="success" class="rounded-md bg-emerald-50 p-3 text-sm text-emerald-700">Cliente registrado: {{ success.fullName }}.</p>
      <button class="btn-primary justify-center" type="submit" :disabled="loading"><LoaderCircle v-if="loading" class="h-4 w-4 animate-spin" /><UserPlus v-else class="h-4 w-4" />{{ loading ? 'Registrando...' : 'Registrar cliente' }}</button>
    </form>
    <dialog ref="clientsDialog" class="w-[min(94vw,900px)] rounded-lg border border-slate-200 bg-white p-0 text-workshop-ink shadow-xl backdrop:bg-slate-950/40">
      <section class="p-5 sm:p-6">
        <header class="flex items-center justify-between gap-4 border-b border-slate-200 pb-4">
          <div><h2 class="text-xl font-black">Clientes registrados</h2><p class="mt-1 text-sm text-slate-500">Sucursal {{ sessionUser.branchId }}</p></div>
          <button class="btn-secondary" type="button" @click="clientsDialog.close()">Cerrar</button>
        </header>
        <p v-if="clientsError" class="mt-4 rounded-md bg-red-50 p-3 text-sm text-red-700">{{ clientsError }}</p>
        <p v-else-if="clientsLoading" class="mt-4 text-sm text-slate-500">Cargando clientes...</p>
        <p v-else-if="!clients.length" class="mt-4 text-sm text-slate-500">Aun no hay clientes registrados en esta sucursal.</p>
        <div v-else class="mt-4 max-h-[55vh] overflow-auto">
          <table class="w-full min-w-[640px] text-left text-sm">
            <thead class="sticky top-0 bg-slate-50 text-workshop-steel"><tr><th class="px-3 py-2">Cliente</th><th class="px-3 py-2">Email</th><th class="px-3 py-2">Telefono</th><th class="px-3 py-2">Registro</th></tr></thead>
            <tbody><tr v-for="client in clients" :key="client.id" class="border-t border-slate-100"><td class="px-3 py-3 font-semibold">{{ client.fullName }}</td><td class="px-3 py-3">{{ client.email }}</td><td class="px-3 py-3">{{ client.personalPhone }}</td><td class="px-3 py-3">{{ formatDate(client.createdAt) }}</td></tr></tbody>
          </table>
        </div>
      </section>
    </dialog>
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { LoaderCircle, UserPlus } from 'lucide-vue-next'
import { ClientFacade } from '../facades/ClientFacade'

const props = defineProps({ sessionUser: { type: Object, required: true } })
const facade = new ClientFacade()
const photo = ref(null), previewUrl = ref(''), error = ref(''), success = ref(null), loading = ref(false)
const clientsDialog = ref(null), clients = ref([]), clientsError = ref(''), clientsLoading = ref(false)
const today = new Date().toISOString().slice(0, 10)
const canRegister = computed(() => ['ADMINISTRATOR', 'RECEPTIONIST'].includes(props.sessionUser.role))
const form = reactive({ fullName:'', alternateContactName:'', age:null, birthDate:'', personalPhone:'', workPhone:'', email:'', workEmail:'', street:'', neighborhood:'', municipality:'', state:'', postalCode:'' })
async function onPhoto(event) { error.value=''; const selected = event.target.files?.[0]; try { const url = await facade.validatePhoto(selected); if (previewUrl.value) URL.revokeObjectURL(previewUrl.value); photo.value=selected; previewUrl.value=url } catch (err) { photo.value=null; event.target.value=''; error.value=err.message } }
async function submit() { error.value=''; success.value=null; loading.value=true; try { success.value = await facade.register(props.sessionUser, form, photo.value) } catch (err) { error.value = err.message || 'No fue posible registrar el cliente.' } finally { loading.value=false } }
async function openClientsDialog() { clientsError.value=''; clientsLoading.value=true; clientsDialog.value.showModal(); try { clients.value = await facade.list(props.sessionUser) } catch (err) { clientsError.value = err.message || 'No fue posible consultar los clientes.' } finally { clientsLoading.value=false } }
function formatDate(value) { return new Intl.DateTimeFormat('es-MX', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) }
</script>
