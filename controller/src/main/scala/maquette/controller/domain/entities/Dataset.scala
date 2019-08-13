package maquette.controller.domain.entities

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import maquette.controller.values.{Authorization, CommittedVersionDetails, DatasetDetails, DatasetPermission, ResourceName, UID, User, Version, VersionDetails, WorkingVersionDetails}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData.Record

import scala.concurrent.Future
import scala.util.Try

trait DataStorageAdapter {

  def write(datasetId: UID, dataId: UID, data: Source[ByteString, NotUsed]): Future[Unit]

  def delete(datasetId: UID, dataId: UID): Unit

  def read(datasetId: UID, dataId: UID): List[ByteString]

}

trait DatasetRepository {

  def findAll: List[Dataset]

  def findDatasetById(id: UID): Option[Dataset]

  def findDatasetByName(namespace: ResourceName, name: ResourceName): Option[Dataset]

  def save(dataset: Dataset)

}

trait Dataset {

  val id: UID

  def append(executor: User, schema: Schema, data: Source[Record, NotUsed]): Future[WorkingVersionDetails]

  def replace(executor: User, schema: Schema, data: Source[Record, NotUsed]): Future[WorkingVersionDetails]

  def canRead(executor: User): Boolean

  def canWrite(executor: User): Boolean

  def commit(executor: User, message: String): Future[CommittedVersionDetails]

  def get(executor: User): Try[Source[ByteString, NotUsed]]

  def get(executor: User, version: Version): Try[Source[ByteString, NotUsed]]

  def grant(executor: User, to: Authorization, permission: DatasetPermission): Unit

  def revoke(executor: User, from: Authorization, permission: DatasetPermission): Unit

  def versions: List[VersionDetails]

}

private[domain] case class DatasetImpl(
                                        id: UID)(
                                        val dataRepository: DataStorageAdapter,
                                        var behavior: DatasetBehavior) extends Dataset {

  override def append(executor: User, schema: Schema, data: Source[Record, NotUsed]): Future[WorkingVersionDetails] = {
    val result = behavior.append(executor, schema, data)
    behavior = result._1
    result._2
  }

  override def replace(executor: User, schema: Schema, data: Source[Record, NotUsed]): Future[WorkingVersionDetails] = {
    val result = behavior.replace(executor, schema, data)
    behavior = result._1
    result._2
  }

  override def canRead(executor: User): Boolean = ???

  override def canWrite(executor: User): Boolean = ???

  override def commit(executor: User, message: String): Future[CommittedVersionDetails] = {
    val result = behavior.commit(executor, message)
    behavior = result._1
    result._2
  }

  override def get(executor: User, version: Version): Try[Source[ByteString, NotUsed]] = ???

  override def grant(executor: User, to: Authorization, permission: DatasetPermission): Unit = ???

  override def revoke(executor: User, from: Authorization, permission: DatasetPermission): Unit = ???

}

sealed trait DatasetBehavior {

  def append(executor: User, schema: Schema, data: Source[Record, NotUsed]): (DatasetBehavior, Future[WorkingVersionDetails])

  def commit(executor: User, message: String): (DatasetBehavior, Future[CommittedVersionDetails])

  def replace(executor: User, schema: Schema, data: Source[Record, NotUsed]): (DatasetBehavior, Future[WorkingVersionDetails])

  def get(executor: User): Try[Source[ByteString, NotUsed]]

  def get(executor: User, version: Version): Try[Source[ByteString, NotUsed]]

}

class EmptyDataset(dataStorageAdapter: DataStorageAdapter) extends DatasetBehavior {

}

class DatasetWithWorkingSet(
                             dataStorageAdapter: DataStorageAdapter,
                             versions: List[CommittedVersionDetails],
                             working: WorkingVersionDetails) extends DatasetBehavior {

}

class DatasetWithCommittedVersions(
                                  dataStorageAdapter: DataStorageAdapter,
                                  versions: List[CommittedVersionDetails]) extends DatasetBehavior {

}

