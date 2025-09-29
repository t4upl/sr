Assumptions:
- General assumption
  - We assume the time we receive event is when event happened. There is no delay between time when event happened and when this information is passed to the board
  - We assume that all operation are equally valid. There is no need for optimaizing for `getGamesSummary()` method. Simple implementation will do. In real life `getGamesSummary` would be used much more than start game or update score
  - We assume teams names are unique
  - We assume there are no false events. All events represent reality 


- Start a game
  - At any time a team may play one game. So we can't have active games "Mexico - Canada" and "Mexico - USA".  
  - For now teams are defined by their names. We don't handle different ways a team may be called: Real Madryt vs REAL MADRYT vs RMA. For us these are three different teams.
  - Endless games problem - we assume that we will always receive "Finish a game" event for a game. In real life we would probbably solve this by some custodian process which would close the games that should end 
- Get a summary of games by total score.
  - Ordering - in order to ensure that the same games in progress return always the same data in same order we will order data first by most recent and then by home team name. We are modyfing the requirement:
    `Those games with the same total score
    will be returned ordered by the most recently added to our system.`