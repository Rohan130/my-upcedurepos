# Linux TARR Implementation

TCP ACK Rate Request (TARR) implementation for Linux kernel 7.0.0-rc2.

Based on IETF draft: draft-ietf-tcpm-ack-rate-request

## Overview
TARR allows a TCP sender to request the receiver's ACK rate.
R = segments per ACK. R=0 means immediate ACK.

## Files
- `tarr-final.patch` — kernel patch (304 lines, 7 files modified)

## Modified Files
- `include/uapi/linux/tcp.h` — TARR constants
- `include/linux/tcp.h` — struct tcp_sock fields
- `include/net/netns/ipv4.h` — sysctl field
- `net/ipv4/tcp_input.c` — option parsing + ACK behavior
- `net/ipv4/tcp_output.c` — option emission
- `net/ipv4/sysctl_net_ipv4.c` — sysctl registration
- `net/ipv4/tcp_ipv4.c` — default value

## Usage
```bash
# Enable TARR with R=3
sudo sysctl -w net.ipv4.tcp_tarr_r=3
```

## License
GPL-2.0

Wireshark decodes as: `TCP Option - Experimental: TCP ACK Rate Request`

## License

GPL-2.0 — consistent with the Linux kernel license.

## References

- IETF Draft: https://datatracker.ietf.org/doc/draft-ietf-tcpm-ack-rate-request/
- RFC 6994: Shared Use of Experimental TCP Options
