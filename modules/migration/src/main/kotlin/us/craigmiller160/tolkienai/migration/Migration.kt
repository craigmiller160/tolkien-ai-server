package us.craigmiller160.tolkienai.migration

interface Migration<T> {
  fun migrate(helper: T)
}
