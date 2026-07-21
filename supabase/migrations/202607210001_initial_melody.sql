-- Melody backend: run with `supabase db push` or paste into the Supabase SQL editor.
create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique not null,
  display_name text not null,
  avatar_url text,
  bio text not null default '',
  is_premium boolean not null default false,
  created_at timestamptz not null default now()
);

create table if not exists public.songs (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  artist_name text not null,
  album text not null default '',
  cover_image_url text not null,
  audio_url text not null,
  is_local boolean not null default false,
  play_count bigint not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists public.playlists (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid references public.profiles(id) on delete cascade,
  title text not null,
  description text not null default '',
  cover_image_url text,
  kind text not null check (kind in ('WORLD','LOCAL','USER')),
  is_public boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists public.playlist_songs (
  playlist_id uuid references public.playlists(id) on delete cascade,
  song_id uuid references public.songs(id) on delete cascade,
  position integer not null,
  primary key (playlist_id, song_id)
);

create table if not exists public.follows (
  follower_id uuid references public.profiles(id) on delete cascade,
  followed_id uuid references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (follower_id, followed_id),
  check (follower_id <> followed_id)
);

create table if not exists public.liked_songs (
  user_id uuid references public.profiles(id) on delete cascade,
  song_id uuid references public.songs(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (user_id, song_id)
);

create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  sender_id uuid not null references public.profiles(id) on delete cascade,
  recipient_id uuid not null references public.profiles(id) on delete cascade,
  body text not null default '',
  shared_song_id uuid references public.songs(id) on delete set null,
  status text not null default 'SENT' check (status in ('SENDING','SENT','DELIVERED','READ','FAILED')),
  created_at timestamptz not null default now(),
  read_at timestamptz
);

create index if not exists songs_search_idx on public.songs using gin
  (to_tsvector('simple', title || ' ' || artist_name || ' ' || album));
create index if not exists messages_conversation_idx on public.messages(sender_id, recipient_id, created_at desc);

alter table public.profiles enable row level security;
alter table public.songs enable row level security;
alter table public.playlists enable row level security;
alter table public.playlist_songs enable row level security;
alter table public.follows enable row level security;
alter table public.liked_songs enable row level security;
alter table public.messages enable row level security;

create policy "catalog readable" on public.songs for select using (true);
create policy "public playlists readable" on public.playlists for select using (is_public or owner_id = auth.uid());
create policy "playlist owner writes" on public.playlists for all using (owner_id = auth.uid()) with check (owner_id = auth.uid());
create policy "playlist songs readable" on public.playlist_songs for select using (true);
create policy "playlist owner edits songs" on public.playlist_songs for all using (
  exists(select 1 from public.playlists p where p.id = playlist_id and p.owner_id = auth.uid())
);
create policy "profiles readable" on public.profiles for select using (true);
create policy "profile owner updates" on public.profiles for update using (id = auth.uid());
create policy "follows readable" on public.follows for select using (true);
create policy "own follows" on public.follows for all using (follower_id = auth.uid()) with check (follower_id = auth.uid());
create policy "own likes" on public.liked_songs for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "conversation messages" on public.messages for select using (sender_id = auth.uid() or recipient_id = auth.uid());
create policy "send messages" on public.messages for insert with check (sender_id = auth.uid());
create policy "recipient receipts" on public.messages for update using (recipient_id = auth.uid() or sender_id = auth.uid());

do $$ begin
  alter publication supabase_realtime add table public.messages;
exception when duplicate_object then null;
end $$;

create or replace function public.handle_new_user() returns trigger language plpgsql security definer as $$
begin
  insert into public.profiles(id, username, display_name)
  values(new.id, 'user_' || substr(new.id::text, 1, 8), coalesce(new.raw_user_meta_data->>'display_name', 'Melody listener'));
  return new;
end $$;
drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created after insert on auth.users for each row execute procedure public.handle_new_user();

insert into storage.buckets(id, name, public)
values ('avatars', 'avatars', true)
on conflict (id) do update set public = true;

create policy "avatar images are public" on storage.objects for select
using (bucket_id = 'avatars');
create policy "users upload own avatar" on storage.objects for insert to authenticated
with check (bucket_id = 'avatars' and (storage.foldername(name))[1] = auth.uid()::text);
create policy "users update own avatar" on storage.objects for update to authenticated
using (bucket_id = 'avatars' and (storage.foldername(name))[1] = auth.uid()::text)
with check (bucket_id = 'avatars' and (storage.foldername(name))[1] = auth.uid()::text);

-- Idempotent 50-track demonstration catalog; replace URLs with licensed production assets before release.
insert into public.songs(title, artist_name, album, cover_image_url, audio_url, is_local)
select
  (array['Afterglow','Blue Horizon','City Lights','Drift','Electric Rain','Far Away','Golden Hour','Home Again','Infinite Road','Jasmine Sky'])[1 + ((n-1) % 10)] || ' ' || n,
  (array['Aria Nova','Caspian Echo','Lena Hart','Neon Atlas','Darya','The Daylights','Soroush Waves','Mira Lane'])[1 + ((n-1) % 8)],
  (array['Fresh Finds','Global Pulse','Persian Nights'])[1 + ((n-1) % 3)],
  'https://picsum.photos/seed/melody-' || n || '/600/600',
  'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-' || (1 + ((n-1) % 16)) || '.mp3',
  n % 3 = 0
from generate_series(1, 50) n
where not exists (select 1 from public.songs);

insert into public.playlists(title, description, cover_image_url, kind, is_public)
select
  (array['Global Focus','World Discovery','International Hits','Persian Classics','Iranian Pop','Local Essentials'])[n],
  case when n <= 3 then 'Music from around the world' else 'Selected Persian music' end,
  'https://picsum.photos/seed/official-playlist-' || n || '/600/600',
  case when n <= 3 then 'WORLD' else 'LOCAL' end,
  true
from generate_series(1, 6) n
where not exists (select 1 from public.playlists where owner_id is null);

insert into public.playlist_songs(playlist_id, song_id, position)
select p.id, picked.id, picked.position
from public.playlists p
cross join lateral (
  select s.id, row_number() over(order by s.created_at, s.id)::integer as position
  from public.songs s
  order by md5(s.id::text || p.id::text)
  limit 12
) picked
where p.owner_id is null
  and not exists (select 1 from public.playlist_songs ps where ps.playlist_id = p.id);
