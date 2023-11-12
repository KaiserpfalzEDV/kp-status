---
title: "Domain Model"
pre: "8.2 "
weight: 2
---

{{< mermaid >}}
classDiagram

    class Metadata {
        +getId() UUID
        +getNamespace() String
        +getName() String
        +isDeleted() boolean
        +getCreated() OffsetDateTime
        +getModified() OffsetDateTime
        +getDeleted() OffsetDateTime
    }

    ServiceRepository ..> Service
    ServiceRepository ..> ServiceHistory
    class ServiceRepository {
        +createService(Service) Service
        +retrieveServiceById(UUID) Service
        +deleteService(Service)
        +getHistory(Service) ServiceHistory
    }

    Service *-- Metadata
    Service ..|> Identifiable
    Service --o Service : subServices
    Service --o Service : dependencies
    Service o-- ServiceState
    Service o-- OperationalLevelAgreement
    class Service {
        -ServiceState state
        -OperationalLevelAgreement[] operationalLevelAgreements
        -Service[] subServices
        -Service[] dependencies
        +getSubServices() Service[]
        +getDependencies() Service[]
        +getLastTransition() OffsetDateTime
        +getState() State
        +getOperationalLevelAgreements() OperationalLevelAgreement[]
        +history() ServiceState[]
        +isUp() boolean
        +isDown() boolean
        +isDegraded() boolean
        +addEvent(ReportEvent)
    }

    ServiceHistory --o Service
    ServiceHistory o-- ServiceState
    class ServiceHistory {
        +getService() Service
        +getHistory() ServiceState[]
    }

    ServiceState ..|> State
    ServiceState ..|> HasDuration
    ServiceState --* Degradation
    class ServiceState {
        -Degradation degradation
        -OffsetDateTime timestamp

        +getDegradation() Degradation
    }

    State ..|> Identifiable
    class State {
        +is(State) boolean
        +isUp() boolean
        +isDown() boolean
        +recover()
        +fail()
    }

    StateEvent --> HasId
    class StateEvent {
        -Service service

        +execute(Service) Service
    }

    FailEvent ..|> StateEvent
    DegradeEvent ..|> StateEvent
    RecoverEvent ..|> StateEvent
    RecoverPartly ..|> DegradeEvent

    ReportDegradedEvent --> ReportEvent
    ReportDownEvent --> ReportEvent
    ReportSolvedEvent --> ReportEvent
    ReportPlannedDowntimeEvent --> ReportEvent
    ChangePlannedDowntimeEvent --> ReportPlannedDowntimeEvent
    CancelPlannedDowntimeEvent --> ChangePlannedDowntimeEvent
    class ChangePlannedDowntimeEvent {
        -UUID plannedDowntimeId
        +getPlannedDowntimeId() UUID
    }

    ReportEvent *-- Metadata
    ReportEvent ..|> HasEstimatedSolutionTime
    ReportEvent --> HasId
    class ReportEvent {
        -String externalDegradationId
        -OffsetDateTime timestamp
        -Service service
        -String story
        -boolean privateInformation

        +getExternalDegradationId() String
        +getTimestamp() OffsetDateTime
        +getService() Service
        +getStory() String
        +isPublic() boolean
        +isPrivate() boolean
    }


    OperationalLevelAgreement ..|> Identifiable
    OperationalLevelAgreement *-- State
    OperationalLevelAgreement *-- Metadata
    class OperationalLevelAgreement {
        -Duration duration
        -double availability
        -State violationState
        +getDuration() Duration 
        +getAvailability() double
        +getViolationState() State
    }

    DegradationService *-- Metadata
    DegradationService ..> Degradation
    DegradationService ..> ReportDegradedEvent
    DegradationService ..> ReportDownEvent
    DegradationService ..> ReportSolvedEvent
    DegradationService ..> ReportPlannedDowntimeEvent
    DegradationService ..> ChangePlannedDowntimeEvent
    DegradationService ..> CancelPlannedDowntimeEvent
    class DegradationService {
        +reportDegradation(ReportDegradedEvent) Degradation
        +solveDegradation(ReportSolvedEvent) Degradation
        +createPlannedDowntime(ReportPlannedDowntimeEvent) Degradation
        +changePlannedDowntime(ChangePlannedDowntimeEvent) Degradation
        +cancelPlannedDowntime(CancelPlannedDowntimeEvent) Degradation

        +retrieveDegradationById(UUID) Degradation
        +retrieveDegradationByExternalDegradationId(String) Degradation
        
        +retrieveUpcomingDegradations(UUID) Degradation[]
        +retrieveUpcomingDegradations(UUID,OffsetDateTime, Duration) Degradation[]
        
        +retrieveUpcomingDegradations() Degradation[]
        +retrieveUpcomingDegradations(OffsetDateTime, Duration) Degradation[]
    }

    Degradation *-- Metadata
    Degradation ..|> HasDuration
    Degradation ..|> State
    Degradation ..|> HasEstimatedSolutionTime
    Degradation --* Service
    Degradation o-- OperatingAgreementViolation
    Degradation o-- DegradationHistory
    class Degradation {
        -String externalDegradationId
        -Service service
        -OperatingAgreementViolation[] violations
        -DegradationHistory[] history
        -String description

        +getExternalDegradationId() String
        +isOngoing() boolean
        +isSolved() boolean
        +getHistory() DegradationHistory[]
        +addHistory(DegradationHistory)
    }

    DegradationHistory ..|> HasId
    class DegradationHistory {
        -OffsetDateTime timestamp
        -String story
        -boolean privateInformation
        +getTimestamp() OffsetDateTime
        +getStory() String
        +isPublic() boolean
        +isPrivate() boolean
    }

    OperatingAgreementViolation ..|> Identifiable
    OperatingAgreementViolation ..|> HasDuration
    OperatingAgreementViolation --* OperationalLevelAgreement
    OperatingAgreementViolation --* Service
    class OperatingAgreementViolation {
        +getOperationalLevelAgreement() OperationalLevelAgreement
        +getService() Service
    }

    class HasEstimatedSolutionTime {
        -OffsetDateTime estimatedSolutionTime
        +getEstimatedSolutionTime() OffsetDateTime
    }

    Identifiable --> HasId
    Identifiable --> HasName

    class HasId {
        -UUID id
        +getId() UUID
        +getDisplayId() String
    }

    class HasName {
        -String name
        +getName() String
        +getDisplayName() String
   }

    class HasDuration {
        -OffsetDateTime start
        -Duration duration
        +getStart() OffsetDateTime
        +getEnd() OffsetDateTime
        +getDuration() Duration
    }
{{< /mermaid >}}

## Possible state transformations

{{< mermaid >}}
stateDiagram-v2
    up : Service available
    down : Service unavailable

    up --> down : fail
    down --> up : recover
{{< /mermaid >}}
