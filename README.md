# packet-id-dumper

Quick and dirty way to dump the packet ids related to a packet identifier / name.
Does not provide any additional packet information.

## Usage

1. Clone the repo
2. Compile the mod
3. Drag into mods folder on either server or client

The mapping file is created in the working directory of the environment:

- Client: `.minecraft/packet_ids.json`
- Server: `%server_dir%/packet_ids.json`