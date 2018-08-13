export type UserLocation = {
  id: string
  label: string
  state: UserLocationState
  stays: UserLocationStay[]
}
export type UserLocationState = number | null
export type UserLocationStay = {
  from: number,
  to: number,
}
