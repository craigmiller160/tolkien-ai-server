package us.craigmiller160.tolkienai.migration

interface MigrationRunner {
  fun run(): List<MigrationReport>
}
