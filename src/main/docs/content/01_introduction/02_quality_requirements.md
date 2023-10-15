+++
title = "Quality Requirements"
menuTitle = "1.2 Quality Requirements"
weight = 16
+++

## 1.2 Quality Requirements

This table describes the central quality requirements of KP-STATUS.
The order is a rough estimate of importance.

| Quality Requirement                             | Motivation and Description |
|-------------------------------------------------|----------------------------|
| High stability | The service is needed when other services are failing. The service needs to be very stable and also publish a notification if the service itself is not working reliable. |
| High security | The software has to be secure so information is only changed by authorized requests. Also the status of some services may require authorization. |
| Fast responses to consumer requests             | Especially during service degradation the consumers need to be able to check the state. The load will increase and slow times will add additional load due to reload-requests of impatient consumers. |
| Fast state updates                              | The propagation of a state chance must be very fast to reflect the correct state of the service availabilities published. |
| Easy maintainability                            | The software has be easily maintained. This means full test coverage. A nice documentation and a readable coding style. |

The [Quality Scenarios in Chapter 10](/10_quality_requirements/) will make these requirements more concrete.
