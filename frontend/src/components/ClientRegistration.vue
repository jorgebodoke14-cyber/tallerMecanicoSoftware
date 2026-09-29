<template>
  <section class="mx-auto w-full max-w-5xl px-4 py-8 sm:px-6">
    <header class="mb-6 flex flex-wrap items-center justify-between gap-4">
      <div>
        <p class="text-sm font-bold text-workshop-teal">Sucursal {{ sessionUser.branchId }}</p>
        <h1 class="text-3xl font-black text-workshop-ink">Registro de clientes</h1>
      </div>
      <button class="btn-secondary" type="button" @click="$emit('logout')"><LogOut class="h-4 w-4" />Cerrar sesion</button>
    </header>
    <div v-if="!canRegister" class="panel p-6 text-sm text-red-700">Solo Administrador del sistema o Recepcionista puede registrar clientes.</div>
    <form v-else class="panel grid gap-6 p-6 sm:p-8" @submit.prevent="submit">
      <div class="grid gap-4 md:grid-cols-2">
        <Field label="Nombre completo"><input v-model.trim="form.fullName" class="field mt-1" required minlength="2" maxlength="120" /></Field>
        <Field label="Contacto alternativo"><input v-model.trim="form.alternateContactName" class="field mt-1" required minlength="2" maxlength="120" /></Field>
        <Field label="Edad"><input v-model.number="form.age" class="field mt-1" type="number" required min="18" max="120" /></Field>
        <Field label="Fecha de nacimiento"><input v-model="form.birthDate" class="field mt-1" type="date" required :max="today" /></Field>
        <Field label="Telefono personal"><input v-model.trim="form.personalPhone" class="field mt-1" required inputmode="numeric" pattern="[0-9]{10}" placeholder="10 digitos" /></Field>
        <Field label="Telefono de trabajo"><input v-model.trim="form.workPhone" class="field mt-1" inputmode="numeric" pattern="[0-9]{10}" placeholder="Opcional, 10 digitos" /></Field>
        <Field label="Email"><input v-model.trim="form.email" class="field mt-1" required type="email" maxlength="160" /></Field>
        <Field label="Email de trabajo"><input v-model.trim="form.workEmail" class="field mt-1" type="email" maxlength="160" /></Field>
      </div>
      <div class="border-t border-slate-200 pt-6"><h2 class="text-lg font-black">Direccion</h2>
        <div class="mt-4 grid gap-4 md:grid-cols-2">
          <Field label="Calle"><input v-model.trim="form.street" class="field mt-1" required /></Field>
          <Field label="Colonia"><input v-model.trim="form.neighborhood" class="field mt-1" required /></Field>
          <Field label="Municipio"><input v-model.trim="form.municipality" class="field mt-1" required /></Field>
          <Field label="Estado"><input v-model.trim="form.state" class="field mt-1" required /></Field>
          <Field label="Codigo postal"><input v-model.trim="form.postalCode" class="field mt-1" required inputmode="numeric" pattern="[0-9]{5}" maxlength="5" /></Field>
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
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { LogOut, LoaderCircle, UserPlus } from 'lucide-vue-next'
import { ClientFacade } from '../facades/ClientFacade'

const props = defineProps({ sessionUser: { type: Object, required: true } })
defineEmits(['logout'])
const facade = new ClientFacade()
const photo = ref(null), previewUrl = ref(''), error = ref(''), success = ref(null), loading = ref(false)
const today = new Date().toISOString().slice(0, 10)
const canRegister = computed(() => ['ADMINISTRATOR', 'RECEPTIONIST'].includes(props.sessionUser.role))
const form = reactive({ fullName:'', alternateContactName:'', age:null, birthDate:'', personalPhone:'', workPhone:'', email:'', workEmail:'', street:'', neighborhood:'', municipality:'', state:'', postalCode:'' })
const Field = { props: ['label'], template: '<label class="block text-sm font-bold text-workshop-steel">{{ label }}<slot /></label>' }
async function onPhoto(event) { error.value=''; const selected = event.target.files?.[0]; try { const url = await facade.validatePhoto(selected); if (previewUrl.value) URL.revokeObjectURL(previewUrl.value); photo.value=selected; previewUrl.value=url } catch (err) { photo.value=null; event.target.value=''; error.value=err.message } }
async function submit() { error.value=''; success.value=null; loading.value=true; try { success.value = await facade.register(props.sessionUser, form, photo.value) } catch (err) { error.value = err.message || 'No fue posible registrar el cliente.' } finally { loading.value=false } }
</script>
