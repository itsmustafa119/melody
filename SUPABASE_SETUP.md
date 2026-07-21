# Supabase setup

1. Create a Supabase project and run `supabase/migrations/202607210001_initial_melody.sql`.
2. Put these values in the user-level Gradle properties file (`~/.gradle/gradle.properties`), never in Git:

   ```properties
   SUPABASE_URL=https://YOUR_PROJECT.supabase.co
   SUPABASE_ANON_KEY=YOUR_PUBLISHABLE_ANON_KEY
   ```

3. Rebuild the app. Without credentials, Melody deliberately uses its bundled 50-track demo catalog.

The migration enables RLS, least-privilege policies, profile creation, full-text catalog indexing,
social relationships, public/user playlists, private messages, receipts, song sharing, and Realtime
publication for messages.
