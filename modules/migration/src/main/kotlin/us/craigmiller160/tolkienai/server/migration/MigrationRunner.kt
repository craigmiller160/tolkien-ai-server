package us.craigmiller160.tolkienai.server.migration

interface MigrationRunner {
  fun run(): List<MigrationReport>
}
