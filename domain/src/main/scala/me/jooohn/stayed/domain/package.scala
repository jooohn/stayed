package me.jooohn.stayed

package object domain {

  trait DomainError {
    def message: String
  }

  type ErrorOr[A] = Either[DomainError, A]

}
