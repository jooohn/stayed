export type UserLocationId = string
export type UserLocation = {
  id: UserLocationId
  label: string
  state: UserLocationState
  stays: UserLocationStay[]
}
export type UserLocationState = number | null
export type UserLocationStay = {
  from: number,
  to: number,
}
