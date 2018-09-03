package io.circe

import java.time.{
  DateTimeException,
  Duration,
  Instant,
  LocalDate,
  LocalDateTime,
  LocalTime,
  OffsetDateTime,
  OffsetTime,
  Period,
  YearMonth,
  ZonedDateTime,
  ZoneId
}
import java.time.format.{ DateTimeFormatter, DateTimeParseException }
import java.time.format.DateTimeFormatter.{
  ISO_LOCAL_DATE,
  ISO_LOCAL_DATE_TIME,
  ISO_LOCAL_TIME,
  ISO_OFFSET_DATE_TIME,
  ISO_OFFSET_TIME,
  ISO_ZONED_DATE_TIME
}

private[circe] object JavaTimeInstances {
  final val yearMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

  /**
   * Adds error information of `DateTimeParseException` to the error message of `DecodingFailure`.
   */
  final def formatMessage(typeName: String, dateTimeParseException: DateTimeParseException): String =
    Option(dateTimeParseException.getMessage).fold(typeName) { errMsg =>
      s"$typeName (DateTimeParseException: $errMsg)"
    }

   /**
    * Adds error information of `DateTimeException` to the error message of `DecodingFailure` for `ZoneId`.
    */
  final def formatZoneIdMessage(dateTimeException: DateTimeException): String =
    Option(dateTimeException.getMessage).fold("ZoneId") { errMsg =>
      s"ZoneId (DateTimeException: $errMsg)"
    }
}

private[circe] trait JavaTimeDecoders {
  /**
   * @group Time
   */
  implicit final val decodeDuration: Decoder[Duration] = Decoder.instance { c =>
    c.as[String] match {
      case Right(s) => try Right(Duration.parse(s)) catch {
        case e: DateTimeParseException =>
          // For some reason the error message for `Duration` does not contain the input string by
          // default.
          Left(DecodingFailure(s"Duration (Text '$s' cannot be parsed to a Duration)", c.history))
      }
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[Duration]]
    }
  }

  /**
   * @group Time
   */
  implicit final val decodeInstant: Decoder[Instant] = Decoder.instance { c =>
    c.as[String] match {
      case Right(s) => try Right(Instant.parse(s)) catch {
        case e: DateTimeParseException =>
          Left(DecodingFailure(JavaTimeInstances.formatMessage("Instant", e), c.history))
      }
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[Instant]]
    }
  }

  /**
   * @group Time
   */
  implicit final val decodePeriod: Decoder[Period] = Decoder.instance { c =>
    c.as[String] match {
      case Right(s) => try Right(Period.parse(s)) catch {
        case _: DateTimeParseException => Left(DecodingFailure("Period", c.history))
          // For some reason the error message for `Period` does not contain the input string by
          // default.
          Left(DecodingFailure(s"Period (Text '$s' cannot be parsed to a Period)", c.history))
      }
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[Period]]
    }
  }

  /**
   * @group Time
   */
  implicit final val decodeZoneId: Decoder[ZoneId] = Decoder.instance { c =>
    c.as[String] match {
      case Right(s) => try Right(ZoneId.of(s)) catch {
        case e: DateTimeException =>
          Left(DecodingFailure(JavaTimeInstances.formatZoneIdMessage(e), c.history))
      }
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[ZoneId]]
    }
  }

  /**
   * @group Time
   */
  final def decodeLocalDate(formatter: DateTimeFormatter): Decoder[LocalDate] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(LocalDate.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("LocalDate", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[LocalDate]]
      }
    }

  /**
   * @group Time
   */
  final def decodeLocalTime(formatter: DateTimeFormatter): Decoder[LocalTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(LocalTime.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("LocalTime", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[LocalTime]]
      }
    }

  /**
   * @group Time
   */
  final def decodeLocalDateTime(formatter: DateTimeFormatter): Decoder[LocalDateTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(LocalDateTime.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("LocalDateTime", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[LocalDateTime]]
      }
    }

  /**
   * @group Time
   */
  final def decodeOffsetTime(formatter: DateTimeFormatter): Decoder[OffsetTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(OffsetTime.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("OffsetTime", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[OffsetTime]]
      }
    }

  /**
   * @group Time
   */
  final def decodeOffsetDateTime(formatter: DateTimeFormatter): Decoder[OffsetDateTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(OffsetDateTime.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("OffsetDateTime", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[OffsetDateTime]]
      }
    }

  /**
   * @group Time
   */
  final def decodeZonedDateTime(formatter: DateTimeFormatter): Decoder[ZonedDateTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(ZonedDateTime.parse(s, formatter)) catch {
          case e: DateTimeParseException =>
            Left(DecodingFailure(JavaTimeInstances.formatMessage("ZonedDateTime", e), c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[ZonedDateTime]]
      }
    }

  /**
   * @group Time
   */
  final def decodeYearMonth(formatter: DateTimeFormatter): Decoder[YearMonth] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) =>
          try Right(YearMonth.parse(s, formatter))
          catch {
            case e: DateTimeParseException =>
              Left(DecodingFailure(JavaTimeInstances.formatMessage("YearMonth", e), c.history))
          }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[YearMonth]]
      }
    }

  /**
   * @group Time
   */
  implicit final def decodeLocalDateDefault: Decoder[LocalDate] = decodeLocalDate(ISO_LOCAL_DATE)

  /**
   * @group Time
   */
  implicit final def decodeLocalTimeDefault: Decoder[LocalTime] = decodeLocalTime(ISO_LOCAL_TIME)

  /**
   * @group Time
   */
  implicit final def decodeLocalDateTimeDefault: Decoder[LocalDateTime] = decodeLocalDateTime(ISO_LOCAL_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def decodeOffsetTimeDefault: Decoder[OffsetTime] = decodeOffsetTime(ISO_OFFSET_TIME)

  /**
   * @group Time
   */
  implicit final def decodeOffsetDateTimeDefault: Decoder[OffsetDateTime] = decodeOffsetDateTime(ISO_OFFSET_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def decodeZonedDateTimeDefault: Decoder[ZonedDateTime] = decodeZonedDateTime(ISO_ZONED_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def decodeYearMonthDefault: Decoder[YearMonth] = decodeYearMonth(JavaTimeInstances.yearMonthFormatter)
}

private[circe] trait JavaTimeEncoders {
  /**
   * @group Time
   */
  implicit final val encodeDuration: Encoder[Duration] = Encoder.instance(duration =>
    Json.fromString(duration.toString)
  )

  /**
   * @group Time
   */
  implicit final val encodeInstant: Encoder[Instant] = Encoder.instance(time =>
    Json.fromString(time.toString)
  )

  /**
   * @group Time
   */
  implicit final val encodePeriod: Encoder[Period] = Encoder.instance(period =>
    Json.fromString(period.toString)
  )

  /**
   * @group Time
   */
  implicit final val encodeZoneId: Encoder[ZoneId] = Encoder.instance(zoneId =>
    Json.fromString(zoneId.getId)
  )

  /**
   * @group Time
   */
  final def encodeLocalDate(formatter: DateTimeFormatter): Encoder[LocalDate] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeLocalTime(formatter: DateTimeFormatter): Encoder[LocalTime] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeLocalDateTime(formatter: DateTimeFormatter): Encoder[LocalDateTime] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeOffsetTime(formatter: DateTimeFormatter): Encoder[OffsetTime] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeOffsetDateTime(formatter: DateTimeFormatter): Encoder[OffsetDateTime] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeZonedDateTime(formatter: DateTimeFormatter): Encoder[ZonedDateTime] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  final def encodeYearMonth(formatter: DateTimeFormatter): Encoder[YearMonth] =
    Encoder.instance(time => Json.fromString(time.format(formatter)))

  /**
   * @group Time
   */
  implicit final def encodeLocalDateDefault: Encoder[LocalDate] = encodeLocalDate(ISO_LOCAL_DATE)

  /**
   * @group Time
   */
  implicit final def encodeLocalTimeDefault: Encoder[LocalTime] = encodeLocalTime(ISO_LOCAL_TIME)

  /**
   * @group Time
   */
  implicit final def encodeLocalDateTimeDefault: Encoder[LocalDateTime] = encodeLocalDateTime(ISO_LOCAL_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def encodeOffsetTimeDefault: Encoder[OffsetTime] = encodeOffsetTime(ISO_OFFSET_TIME)

  /**
   * @group Time
   */
  implicit final def encodeOffsetDateTimeDefault: Encoder[OffsetDateTime] = encodeOffsetDateTime(ISO_OFFSET_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def encodeZonedDateTimeDefault: Encoder[ZonedDateTime] = encodeZonedDateTime(ISO_ZONED_DATE_TIME)

  /**
   * @group Time
   */
  implicit final def encodeYearMonthDefault: Encoder[YearMonth] = encodeYearMonth(JavaTimeInstances.yearMonthFormatter)
}
