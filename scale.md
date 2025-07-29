### Options to scale
To scale the solution, will change the approach in each API's 
* For Top-10 rated API,
  - I will ask more mandatory parameter, which will help me fetch less records from DB 
  - Parameters like 
    - year (Required)
    - box office collection > some amount (optional)
      - Genre (Optional)
  - Will add index for specific queries
* Get whether movie is an Oscar winner
  - Will store release date
  - Cron to update oscar flag and box office value for latest movies (Consider release in last 6 months)
* For rate API
  - Will introduce new table for user level ratings.
  - Each user can add review, each user can only add 1 review or update their review
  - Will calculate the avg from all the users and assign it to rate table as source "UserRating"

Apart from API, for security :
* Will replace the static token to either JWT or OAuth2 
* Create session using this auth, and use this session to pass user info to API's
  * Will need userId when we add review.
  