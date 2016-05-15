package spring.bar.api.model

import java.util.UUID

/*
final case class Business(uuid: UUID, name: String)

[
{ "uuid": $val, "name": $val },
{   ...}
]
 */

/*
sealed trait UserRole
case object JourneyManager extends UserRole
case object Member extends UserRole
[
"journeyManager", "member"
]
 */

/*
final case class BusinessToUser(userUuid: UUID, businessUuid: UUID, role: UserRole)
{"userUuid": $val, "businessUuid": $val, "role": $role}

-> Created (with location)
-> Conflict
 */

/*
 {
  uuid: $val,
  email: $val,
  firstName: $val,
  lastName: $val,
  state: $val,
  permissions: ["accessBar", ...],
  businesses: [ {"uuid": $val, "role": $role} ]
  }

 */

// Templates are immutable, as there is single assesment, template should not be modified, but *cloned*
final case class Template(id: Long)

final case class Stage(id: String, templateId: Long)

sealed trait QuestionType
case object Rating extends QuestionType

// Questions should id per template starting from 1, i.e., template1-q1, template1-q2,
// template2-q1, template2-q2...
final case class Question(templateId: Long,
                          id: Long,
                          stageId: String,
                          tpe: QuestionType,
                          title: String,
                          description: String,
                          text: String,
                          labelMin: String,
                          labelMax: String)

//////////

// Assesment is one single "filling" of template.
final case class Assesment(id: Long, templateId: Long)

// Answer describes for which assesment and which question, what did user answer
final case class Answer(assesmentId: Long, questionId: Long, value: Int)
