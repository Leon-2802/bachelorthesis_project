### LocationInformationResponse
#### Structure:
- LocationResults untereinander
- Jedes LocationResult hat: 
- Location:
  - -> StopPoint -> StopPointRef (TriasID), StopPointName
  - -> LocationName
  - -> GeoPosition -> Long/Lat
- Complete: True/False
- Probability: Number between 0 and 1

### TripResponse
#### Structure:
- TripResult -> ResultID, Trip:
  - Duration
  - StartTime + EndTime
  - Interchanges
  - Distance in M
  - TripLeg(s):
    - LegId
    - ContinuousLeg (Option 1):
      - LegStart -> GeoPos oder StopPointRef (ob Station oder random Ort) + LocationName
      - LegEnd (gleich aufgebaut wie LegStart)
      - TimeWindowStart
      - TimeWindowEnd
      - Duration
      - Length (in M)
    - TimedLeg (Option 2):
      - LegBoard -> StopPointRef + -Name + (PlannedBay -> Text) + ServiceDeparture -> TimetabledTime, StopSequenceNumber
      - LegAlight (gleicher Aufbau, nur ServiceArrival anstatt -Departure)
      - Service: 
        - JourneyRef
        - ServiceSection:
          - LineRef
          - DirectionRef
          - Mode -> PtMode (tram zb), Name (87423 SWEG Südwestdeutsche Landesverkehrs-GmbH zb)
          - PublishedLineName
        - (Attributes mit einem Text Objekt als Child, zb Infos zur Barrierefreiheit und so)
        - OriginText -> Text
        - DestinationText -> Text
    - InterchangeLeg
      - InterchangeMode (walk meistens)
      - LegStart -> StopPointRef + LocationName
      - LegEnd -> StopPointRef + LocationName
      - TimeWindowStart
      - TimeWindowEnd
      - Duration
      - WalkDuration
      - BufferTime