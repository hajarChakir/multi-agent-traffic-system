# multi-agent-traffic-system

It is a question of developing an application for simulating road traffic
we will imagine a simplified situation by considering the case of a circular route with single voice, in other words a racing circuit where
cars would not have the right to overtake. This application brings into play:

Vehicle:an agent who can interact with other vehicles and with the road agent where he is and the crossroads agent (lights and priorities)

Road: limited by 2 crossroads, this agent informs his vehicles of events and the vehicles act accordingly.

Carrefour: manages the scheduling of cars according to their destination and the state of the crossroads (red, green lights, and permitted routes)
