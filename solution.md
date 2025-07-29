
#### Detailed Solution
This application lets users check if a movie won a "Best Picture" Oscar, view detailed ratings (including user ratings), and update them. Movie data is enriched in real time using the OMDb API, which provides ratings and box office info. Oscar win status is determined from both a local CSV file and verified via the API.

#### Files & folder structure
We structured the project following a layered architecture: 
* The externalservice package handles external API calls (To call OMDB API)
* The service package contains business logic for enriching movie data, updating movie info & update ratings
* The model package represents internal domain objects.
* The security package is to validate the request. Expects the static API token.


### Diagrams
* Overall architecture diagram with flows followed by numbers and colour
![Solution Diagram](/images/solution.png)
* Flow Diagram
  ![Flow Diagram](/images/flow.png)
* ER Diagram
  ![ER Diagram](/images/ERD.png)