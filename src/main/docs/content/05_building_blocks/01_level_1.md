+++
title = "Level 1"
pre = "5.1 "
weight = 11
+++

## 5.1 Level 1

KP-STATUS is build from five sub systems.
The dotted arrows are business dependencies of the different sub systems ("x -> y" means "x is dependent on y").
The systems on the outside are the interaction points to external systems ([→ 3.2 Technical Context](/03_context/02_technical_context/)).


{{< mermaid >}}
graph TD;
    Core(Core) -->API(API)
    Mock(Mock) -->API
    DS(Data Service) -->API
    RS(Service Runtime) -->Core
    RS --> Mock
    RS --> DS
    WebUI(Web UI) --> RS
{{< /mermaid >}}
*Diagram: kp-status, Building Blocks, Level 1*

----

| Sub System | Description |
|-----------|------------------|
| [API](/05_building_blocks/07_api/) | The core domain model interfaces and the service API definition of KP-STATUS. This is a pure technical building block. |
| [Core](/05_building_blocks/02_implementation/) | The implementation of the domain model .|
| [Mock](/05_building_blocks/03_mock_implementation/) | The mock implementation of the domain model. |
| [Data Service](/05_building_blocks/04_data_service/) | The data storing service. |
| [Service Runtime](/05_building_blocks/05_service_runtime/) | The runtime service (REST API). |
| [UI](/05_building_blocks/06_web_ui/) | The graphical user interface. |
*Table: Overview of the sub systems of the system kp-status*

Section [→ 6.1 Runtime Walkthrough](/06_runtime/01_walkthrough/)) displays the orchestration of the different sub systems during runtime.
