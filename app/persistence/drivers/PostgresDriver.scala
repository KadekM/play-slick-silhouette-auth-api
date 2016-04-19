package persistence.drivers

import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.time.LocalDateTime

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}

trait PostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupport
  with PgRangeSupport
  with PgHStoreSupport
  with PgPlayJsonSupport
  with PgSearchSupport
  //with PgPostGISSupport
  with PgNetSupport
  with PgLTreeSupport {

  def pgjson: String = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  override val api: PostgresDriverApi = new PostgresDriverApi {}

  trait PostgresDriverApi extends API with ArrayImplicits
    with DateTimeImplicits
    //with JsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants {

    implicit val strListTypeMapper: DriverJdbcType[List[String]] = new SimpleArrayJdbcType[String]("text").to(_.toList)

    private class LocalDateTimeDriverType extends DriverJdbcType[LocalDateTime] {
      def sqlType: Int = java.sql.Types.TIMESTAMP_WITH_TIMEZONE
      def setValue(v: LocalDateTime, p: PreparedStatement, idx: Int): Unit = p.setTimestamp(idx, Timestamp.valueOf(v))
      def getValue(r: ResultSet, idx: Int): LocalDateTime = r.getTimestamp(idx).toLocalDateTime
      def updateValue(v: LocalDateTime, r: ResultSet, idx: Int): Unit = r.updateTimestamp(idx, Timestamp.valueOf(v))
      override def valueToSQLLiteral(value: LocalDateTime): String = "{d '" + value.toString + "'}"
    }

    implicit val localDateTimeMapper: DriverJdbcType[LocalDateTime] = new LocalDateTimeDriverType()

    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }
}

