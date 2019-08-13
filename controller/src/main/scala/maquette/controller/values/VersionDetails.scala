package maquette.controller.values

import java.time.Instant

import org.apache.avro.Schema


case class Commit(version: Version, message: String, by: User, date: Instant)


sealed trait VersionDetails

case class WorkingVersionDetails(id: UID, schema: Schema, records: Int, lastModified: Instant, lastModifiedBy: User) extends VersionDetails

case class CommittedVersionDetails(id: UID, schema: Schema, records: Int, commit: Commit) extends VersionDetails