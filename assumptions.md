### Assumptions
The application supports three core actions, each with specific behavior and data flow:

1. Check if a Movie Won an Oscar
   - The system fetches movie details from the database. 
   - If the movie is not found or lacks rating and box office information, it retrieves the full details from the OMDb API. 
   - The retrieved data is then persisted in the database for future use.
   - boxOffice will always be USD
2. Rate a Movie 
   - The system allows rating of movies already stored in the database. 
   - Ratings obtained from the OMDb API it has their own source and value. 
   - User-submitted ratings are stored same table with source as UserRating.
   - Rating will be between 1 to 10 
   - The application assumes a single user is submitting ratings, so no user management or user level authentication is required at this stage.

3. Fetch Top 10 Rated Movies
   - The top 10 movies are retrieved only from the database (no external API calls). 
   - The ranking is based on the stored ratings (including user ratings and/or external ratings if applicable).

Only movies that have already been persisted in the database are considered