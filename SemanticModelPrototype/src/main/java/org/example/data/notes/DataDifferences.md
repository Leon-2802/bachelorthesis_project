### Content
- KVV seems to be unable to process trips based on Coordinates
  - KVV no result for Request from Rue Massenet, Strasbourg -> Offenburg Badstraße 22a (based on cords)
    -> VRN delivers results, even though outside VRN region
  - Same for Trip 02 from tripcoords.md

- KVV vs. VRN Station to Station Trip
  - TripFares: VRN nur SemesterTicket (mit falschem Preis) und D-Ticket, KVV natürlich das ganze Angebot
  - In Service unter Attribute und DestinationText noch Info zu Unplanned, Cancelled und Deviation bei KVV
  - Zu Stops in LegBoard/LegAlight Info zu DemandStop, UnplannedStop, NotServicedStop, NoBoardingAtStop, NoAlightingAtStop bei KVV
  - Wenn Trip in VRN Gebiet liegt:
    - Bei VRN Fares zwar komplett, aber anders als bei KVV (bei Strecken im KVV-Gebiet) keine weiteren Angaben wie in den oberen zwei Punkten
    - Allerdings bei VRN Trias-Situations am Anfang der Response enthalten
    - Bei KVV keine Situations und keine Fares, aber wieder die Extra Infos zu Service und Stop (Wie oben)

### Syntax

### Semantics
- In Service section different semantics for values
  - ie. PublishedLineName VRN: RB68; KVV: R-Bahn RB68
  - In Attribute section different semantics: VRN writes affected LineName before information, KVV only the info 