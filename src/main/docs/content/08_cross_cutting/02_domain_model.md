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
    up --> downSelf : fail(self)
    up --> downSub : fail(sub)
    up --> downDep : fail(dep)
    up --> up : recover(self)
    up --> up : recover(sub)
    up --> up : recover(dep)

    downSelf : Service unavailable
    downSelf --> downMultiSelf : fail(self)
    downSelf --> downSelfSub : fail(sub)
    downSelf --> downSelfDep : fail(dep)
    downSelf --> up : recover(self)
    downSelf --> downSelf : recover(sub)
    downSelf --> downSelf : recover(dep)

    downMultiSelf : Service has multiple unavailabilites
    downMultiSelf --> downMultiSelf : fail(self)
    downMultiSelf --> downMultiSelfSub : fail(sub)
    downMultiSelf --> downMultiSelfDep : fail(dep)
    downMultiSelf --> downMultiSelf : recover(self)
    downMultiSelf --> downSelf : recover(self)
    downMultiSelf --> downMultiSelf : recover(sub)
    downMultiSelf --> downMultiSelf : recover(dep)

    downSub : Sub unavailable
    downSub --> downSub : fail(self)
    downSub --> downMultiSub : fail(sub)
    downSub --> downSubDep : fail(dep)
    downSub --> downSub : recover(self)
    downSub --> up : recover(sub)
    downSub --> downSub : recover(dep)

    downMultiSub : multiple Sub unavailable
    downMultiSub --> downSelfMultiSub : fail(self)
    downMultiSub --> downMultiSub : fail(sub)
    downMultiSub --> downMultiSubDep : fail(dep)
    downMultiSub --> downMultiSub : recover(self)
    downMultiSub --> downMultiSub : recover(sub)
    downMultiSub --> downSub : recover(sub)
    downMultiSub --> downMultiSub : recover(dep)

    downDep : Dependency unavailable
    downDep --> downSelfDep : fail(self)
    downDep --> downSubDep : fail(sub)
    downDep --> downMultiDep : fail(dep)
    downDep --> downDep : recover(self)
    downDep --> downDep : recover(sub)
    downDep --> up : recover(dep)

    downMultiSubDep : Multiple Sub + Dependency unavailable
    downMultiSubDep --> downMultiSelfMultiSubDep : fail(self)
    downMultiSubDep --> downMultiSubDep : fail(sub)
    downMultiSubDep --> downMultiSubMultiDep : fail(dep)
    downMultiSubDep --> downMultiSubDep : recover(self)
    downMultiSubDep --> downMultiSubDep : recover(sub)
    downMultiSubDep --> downSubDep : recover(sub)
    downMultiSubDep --> downMultiSub : recover(dep)

    downMultiDep : Multiple Dependencies unavailable
    downMultiDep --> downMultiSelfMultiDep : fail(self)
    downMultiDep --> downSubMultiDep : fail(sub)
    downMultiDep --> downMultiDep : fail(dep)
    downMultiDep --> downMultiDep : recover(self)
    downMultiDep --> downMultiDep : recover(sub)
    downMultiDep --> downMultiDep : recover(dep)
    downMultiDep --> downDep : recover(dep)

    downSelfSub : Service + Sub unavailable
    downSelfSub --> downMultiSelfSub : fail(self)
    downSelfSub --> downSelfMultiSub : fail(sub)
    downSelfSub --> downSelfSubDep : fail(dep)
    downSelfSub --> downSub : recover(self)
    downSelfSub --> downSelf : recover(sub)
    downSelfSub --> downSelfSub : recover(dep)

    downSelfMultiSub : Service + multiple Sub unavailable
    downSelfMultiSub --> downMultiSelfMultiSub : fail(self)
    downSelfMultiSub --> downSelfMultiSub : fail(sub)
    downSelfMultiSub --> downSelfMultiSubDep : fail(dep)
    downSelfMultiSub --> downMultiSub : recover(self)
    downSelfMultiSub --> downSelfSub : recover(sub)
    downSelfMultiSub --> downSelfMultiSub : recover(sub)
    downSelfMultiSub --> downSelfMultiSub : recover(dep)

    downSelfDep : Service + Dependency unavailable
    downSelfDep --> downMultiSelfDep : fail(self)
    downSelfDep --> downSelfSubDep : fail(sub)
    downSelfDep --> downSelfMultiDep : fail(dep)
    downSelfDep --> downDep : recover(self)
    downSelfDep --> downSelfDep : recover(sub)
    downSelfDep --> downSelf : recover(dep)

    downSelfMultiDep : Service + Multiple Dependencies unavailable
    downSelfMultiDep --> downMultiSelfMultiDep : fail(self)
    downSelfMultiDep --> downSelfSubMultiDep : fail(sub)
    downSelfMultiDep --> downSelfMultiDep : fail(dep)
    downSelfMultiDep --> downMultiDep : recover(self)
    downSelfMultiDep --> downSelfMultiDep : recover(sub)
    downSelfMultiDep --> downSelfMultiDep : recover(dep)
    downSelfMultiDep --> downSelfDep : recover(dep)


    downSelfSubDep: Service + Sub + Dependency unavailable
    downSelfSubDep --> downMultiSelfSubDep : fail(self)
    downSelfSubDep --> downSelfMultiSubDep : fail(sub)
    downSelfSubDep --> downSelfSubMultiDep : fail(dep)
    downSelfSubDep --> downSubDep : recover(self)
    downSelfSubDep --> downSelfDep : recover(sub)
    downSelfSubDep --> downSelfSub : recover(dep)

    downSelfMultiSubDep : Service + multiple Sub + Dependency unavailable
    downSelfMultiSubDep --> downMultiSelfMultiSubDep : fail(self)
    downSelfMultiSubDep --> downSelfMultiSubDep : fail(sub)
    downSelfMultiSubDep --> downSelfMultiSubMultiDep : fail(dep)
    downSelfMultiSubDep --> downMultiSubDep : recover(self)
    downSelfMultiSubDep --> downSelfMultiSubDep : recover(sub)
    downSelfMultiSubDep --> downSelfSubDep : recover(sub)
    downSelfMultiSubDep --> downSelfMultiSub : recover(dep)

    downSelfSubMultiDep : Service + Sub + multiple Dependencies unavailable
    downSelfSubMultiDep --> downMultiSelfSubMultiDep : fail(self)
    downSelfSubMultiDep --> downSelfMultiSubMultiDep : fail(sub)
    downSelfSubMultiDep --> downSelfSubMultiDep : fail(dep)
    downSelfSubMultiDep --> downSubMultiDep : recover(self)
    downSelfSubMultiDep --> downSelfMultiDep : recover(sub)
    downSelfSubMultiDep --> downSelfSubMultiDep : recover(dep)
    downSelfSubMultiDep --> downSelfSubDep : recover(dep)

    downSelfMultiSubMultiDep: Service + multiple Sub + multiple Dependencies unavailable
    downSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : fail(self)
    downSelfMultiSubMultiDep --> downSelfMultiSubMultiDep : fail(sub)
    downSelfMultiSubMultiDep --> downSelfMultiSubMultiDep : fail(dep)
    downSelfMultiSubMultiDep --> downMultiSubMultiDep : recover(self)
    downSelfMultiSubMultiDep --> downSelfMultiSubMultiDep : recover(sub)
    downSelfMultiSubMultiDep --> downSelfSubMultiDep : recover(sub)
    downSelfMultiSubMultiDep --> downSelfMultiSubMultiDep : recover(dep)
    downSelfMultiSubMultiDep --> downSelfMultiSubDep : recover(dep)

    downMultiSelfSub : Multiple Service + Sub unavailable
    downMultiSelfSub --> downMultiSelfSub : fail(self)
    downMultiSelfSub --> downMultiSelfMultiSub : fail(sub)
    downMultiSelfSub --> downMultiSelfSubDep : fail(dep)
    downMultiSelfSub --> downMultiSelfSub : recover(self)
    downMultiSelfSub --> downSelfSub : recover(self)
    downMultiSelfSub --> downMultiSelf : recover(sub)
    downMultiSelfSub --> downMultiSelfSub : recover(dep)

    downMultiSelfMultiSub : Multiple Service + multiple Sub unavailable
    downMultiSelfMultiSub --> downMultiSelfMultiSub : fail(self)
    downMultiSelfMultiSub --> downMultiSelfMultiSub : fail(sub)
    downMultiSelfMultiSub --> downMultiSelfMultiSubDep : fail(dep)
    downMultiSelfMultiSub --> downMultiSelfMultiSub : recover(self)
    downMultiSelfMultiSub --> downSelfMultiSub : recover(self)
    downMultiSelfMultiSub --> downMultiSelfSub : recover(sub)
    downMultiSelfMultiSub --> downMultiSelfMultiSub : recover(sub)
    downMultiSelfMultiSub --> downMultiSelfMultiSub : recover(dep)

    downMultiSelfDep : Multiple Service + Dependency unavailable
    downMultiSelfDep --> downMultiSelfDep : fail(self)
    downMultiSelfDep --> downMultiSelfSubDep : fail(sub)
    downMultiSelfDep --> downMultiSelfMultiDep : fail(dep)
    downMultiSelfDep --> downMultiSelfDep : recover(self)
    downMultiSelfDep --> downSelfDep : recover(self)
    downMultiSelfDep --> downMultiSelfDep : recover(sub)
    downMultiSelfDep --> downMultiSelf : recover(dep)

    downMultiSelfMultiDep : Multiple Service + Multiple Dependencies unavailable
    downMultiSelfMultiDep --> downMultiSelfMultiDep : fail(self)
    downMultiSelfMultiDep --> downMultiSelfSubMultiDep : fail(sub)
    downMultiSelfMultiDep --> downMultiSelfMultiDep : fail(dep)
    downMultiSelfMultiDep --> downMultiSelfMultiDep : recover(self)
    downMultiSelfMultiDep --> downSelfMultiDep : recover(self)
    downMultiSelfMultiDep --> downMultiSelfMultiDep : recover(sub)
    downMultiSelfMultiDep --> downMultiSelfMultiDep : recover(dep)
    downMultiSelfMultiDep --> downMultiSelfDep : recover(dep)


    downMultiSelfSubDep: Multiple Service + Sub + Dependency unavailable
    downMultiSelfSubDep --> downMultiSelfSubDep : fail(self)
    downMultiSelfSubDep --> downMultiSelfMultiSubDep : fail(sub)
    downMultiSelfSubDep --> downMultiSelfSubMultiDep : fail(dep)
    downMultiSelfSubDep --> downMultiSelfSubDep : recover(self)
    downMultiSelfSubDep --> downSelfSubDep : recover(self)
    downMultiSelfSubDep --> downMultiSelfDep : recover(sub)
    downMultiSelfSubDep --> downMultiSelfSub : recover(dep)

    downMultiSelfMultiSubDep : Multiple Service + multiple Sub + Dependency unavailable
    downMultiSelfMultiSubDep --> downMultiSelfMultiSubDep : fail(self)
    downMultiSelfMultiSubDep --> downMultiSelfMultiSubDep : fail(sub)
    downMultiSelfMultiSubDep --> downMultiSelfMultiSubMultiDep : fail(dep)
    downMultiSelfMultiSubDep --> downMultiSelfMultiSubDep : recover(self)
    downMultiSelfMultiSubDep --> downSelfMultiSubDep : recover(self)
    downMultiSelfMultiSubDep --> downMultiSelfMultiSubDep : recover(sub)
    downMultiSelfMultiSubDep --> downMultiSelfSubDep : recover(sub)
    downMultiSelfMultiSubDep --> downMultiSelfMultiSub : recover(dep)

    downMultiSelfSubMultiDep : Multiple Service + Sub + multiple Dependencies unavailable
    downMultiSelfSubMultiDep --> downMultiSelfSubMultiDep : fail(self)
    downMultiSelfSubMultiDep --> downMultiSelfMultiSubMultiDep : fail(sub)
    downMultiSelfSubMultiDep --> downMultiSelfSubMultiDep : fail(dep)
    downMultiSelfSubMultiDep --> downMultiSelfSubMultiDep : recover(self)
    downMultiSelfSubMultiDep --> downSelfSubMultiDep : recover(self)
    downMultiSelfSubMultiDep --> downMultiSelfMultiDep : recover(sub)
    downMultiSelfSubMultiDep --> downMultiSelfSubMultiDep : recover(dep)
    downMultiSelfSubMultiDep --> downMultiSelfSubDep : recover(dep)

    downMultiSelfMultiSubMultiDep: Multiple Service + multiple Sub + multiple Dependencies unavailable
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : fail(self)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : fail(sub)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : fail(dep)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : recover(self)
    downMultiSelfMultiSubMultiDep --> downSelfMultiSubMultiDep : recover(self)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : recover(sub)
    downMultiSelfMultiSubMultiDep --> downMultiSelfSubMultiDep : recover(sub)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubMultiDep : recover(dep)
    downMultiSelfMultiSubMultiDep --> downMultiSelfMultiSubDep : recover(dep)


    downSubDep: Sub + Dependency unavailable
    downSubDep --> downSelfSubDep : fail(self)
    downSubDep --> downMultiSubDep : fail(sub)
    downSubDep --> downSubMultiDep : fail(dep)
    downSubDep --> downSubDep : recover(self)
    downSubDep --> downDep : recover(sub)
    downSubDep --> downSub : recover(dep)

    downSubMultiDep : Sub + multiple Dependencies unavailable
    downSubMultiDep --> downSelfSubMultiDep : fail(self)
    downSubMultiDep --> downMultiSubMultiDep : fail(sub)
    downSubMultiDep --> downSubMultiDep : fail(dep)
    downSubMultiDep --> downSubMultiDep : recover(self)
    downSubMultiDep --> downMultiDep : recover(sub)
    downSubMultiDep --> downSubMultiDep : recover(dep)
    downSubMultiDep --> downSubDep : recover(dep)

    downMultiSubMultiDep: Multiple Sub + multiple Dependencies unavailable
    downMultiSubMultiDep --> downSelfMultiSubMultiDep : fail(self)
    downMultiSubMultiDep --> downMultiSubMultiDep : fail(sub)
    downMultiSubMultiDep --> downMultiSubMultiDep : fail(dep)
    downMultiSubMultiDep --> downMultiSubMultiDep : recover(self)
    downMultiSubMultiDep --> downMultiSubMultiDep : recover(sub)
    downMultiSubMultiDep --> downSubMultiDep : recover(sub)
    downMultiSubMultiDep --> downMultiSubMultiDep : recover(dep)
    downMultiSubMultiDep --> downMultiSubDep : recover(dep)
{{< /mermaid >}}
