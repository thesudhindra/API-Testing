#!/usr/bin/env python3
"""Generate a clean, user-friendly PDF from API-TEST-SCENARIOS.md."""

from __future__ import annotations

import html
import re
from datetime import date
from pathlib import Path

from xhtml2pdf import pisa

DOCS = Path(__file__).parent
SOURCE_MD = DOCS / "API-TEST-SCENARIOS.md"
OUTPUT_PDF = DOCS / "API-TEST-SCENARIOS.pdf"
OUTPUT_HTML = DOCS / "API-TEST-SCENARIOS.html"
OUTPUT_GUIDE_MD = DOCS / "API-TEST-SCENARIOS-GUIDE.md"

CRITICAL_SCENARIOS = [
    ("008–010", "JWT login (customer, ops, admin)", "Obtain Bearer tokens before any protected Banking/Enterprise call"),
    ("011", "Get current user", "Verify JWT is valid and party context is correct"),
    ("022", "BOLA — access other party", "Security: customer must get 403 on another party's data"),
    ("033–035", "Payment happy path", "Create, get, and list payments with JWT + Idempotency-Key"),
    ("036", "Payment without auth", "Security: unauthenticated payment must return 401"),
    ("037–039", "Transfer flow", "Internal transfer with idempotency protection"),
    ("043", "Card authorization", "Tokenised card spend simulation with idempotency"),
    ("075–078", "Fraud screening & alerts", "High-value transaction triggers velocity alert"),
    ("086", "Admin endpoint as customer", "Role-based access: customer cannot read admin settings"),
    ("129", "Idempotency replay (lab)", "Same key + body replays without double debit"),
    ("130", "Fraud velocity alert (lab)", "End-to-end fraud scenario walkthrough"),
    ("131", "Webhook retry & DLQ (lab)", "Async delivery failures and dead-letter queue"),
    ("132", "Concurrent transfer race (lab)", "Optimistic locking under parallel transfers"),
    ("133", "BOLA party access (lab)", "Authorization boundary for cross-party reads"),
    ("134", "Invalid login password", "Authentication failure returns 401"),
    ("135", "Wrong tenant header", "JWT tenant must match X-Tenant-Id header"),
    ("136", "Idempotency conflict", "Same key with different body returns 409"),
    ("137", "Insufficient funds", "Business rule: payment over balance returns 400"),
    ("138", "Lab without auth", "Protected lab endpoints require Basic auth"),
]

TOKEN_FLOW = [
    ("1", "Run Scenario 008", "POST /v1/auth/login as customer → save accessToken to jwt_token"),
    ("2", "Run Scenario 009 or 010", "Login as ops/admin → save accessToken to admin_jwt"),
    ("3", "Use Bearer header", "Authorization: Bearer {{jwt_token}} on Banking & Enterprise calls"),
    ("4", "Set tenant header", "X-Tenant-Id: tenant-demo (must match token tenant)"),
    ("5", "Mutating payments/transfers", "Add Idempotency-Key: {{idempotency_key}} (auto-generated in Postman)"),
]


def strip_md_inline(text: str) -> str:
    text = re.sub(r"\*\*(.+?)\*\*", r"\1", text)
    text = re.sub(r"`(.+?)`", r"\1", text)
    text = re.sub(r"_{2,}", "", text)
    return text.strip()


def esc(text: str) -> str:
    return html.escape(strip_md_inline(text))


def parse_scenarios(content: str) -> list[dict]:
    blocks = re.split(r"\n---\n", content)
    scenarios: list[dict] = []
    for block in blocks:
        m = re.search(r"^## Scenario (\d{3}) — (.+)$", block, re.MULTILINE)
        if not m:
            continue
        num, name = m.group(1), m.group(2)

        def field(label: str) -> str:
            fm = re.search(rf"\*\*{label}:\*\*\s*(.+)", block)
            return strip_md_inline(fm.group(1)) if fm else ""

        headers: list[tuple[str, str]] = []
        headers_section = re.search(r"### Headers\n\n(.*?)(?=\n### |\Z)", block, re.DOTALL)
        if headers_section:
            for row in re.findall(r"\| (.+?) \| `?(.+?)`? \|", headers_section.group(1)):
                if row[0] not in ("Header", "—"):
                    headers.append((row[0], strip_md_inline(row[1])))

        body_section = re.search(r"### Request Body\n\n(.*?)(?=\n### Expected Response)", block, re.DOTALL)
        body = ""
        if body_section:
            raw = body_section.group(1).strip()
            if raw.lower() != "none":
                body = re.sub(r"^```json\n|\n```$", "", raw, flags=re.MULTILINE).strip()

        resp_section = re.search(r"### Expected Response\n\n(.*?)(?=\n\*\*Notes:|\n---|\Z)", block, re.DOTALL)
        status = ""
        response = ""
        if resp_section:
            rs = resp_section.group(1)
            sm = re.search(r"\*\*Status Code:\*\*\s*`?([^`\n]+)`?", rs)
            if sm:
                status = strip_md_inline(sm.group(1))
            if "None (empty)" in rs:
                response = ""
            else:
                jm = re.search(r"```json\n(.*?)```", rs, re.DOTALL)
                if jm:
                    response = jm.group(1).strip()

        notes_m = re.search(r"\*\*Notes:\*\*\s*(.+)", block)
        notes = strip_md_inline(notes_m.group(1)) if notes_m else ""

        scenarios.append({
            "num": num,
            "name": name,
            "url": field("URL"),
            "method": field("Method"),
            "auth": field("Authentication"),
            "headers": headers,
            "body": body,
            "status": status,
            "response": response,
            "notes": notes,
        })
    return scenarios


def parse_index(content: str) -> list[tuple[str, str]]:
    items: list[tuple[str, str]] = []
    in_index = False
    for line in content.splitlines():
        if line.startswith("## Scenario Index"):
            in_index = True
            continue
        if in_index and line.startswith("---"):
            break
        if in_index:
            m = re.match(r"- \*\*Scenario (\d{3})\*\* — (.+)", line)
            if m:
                items.append((m.group(1), strip_md_inline(m.group(2))))
    return items


def scenario_count(content: str) -> int:
    m = re.search(r"Complete Reference \((\d+) scenarios\)", content)
    return int(m.group(1)) if m else len(parse_scenarios(content))


def build_intro_html(count: int) -> str:
    critical_rows = "".join(
        f"<tr><td>{esc(s)}</td><td>{esc(title)}</td><td>{esc(desc)}</td></tr>"
        for s, title, desc in CRITICAL_SCENARIOS
    )
    token_rows = "".join(
        f"<tr><td>{esc(step)}</td><td>{esc(action)}</td><td>{esc(detail)}</td></tr>"
        for step, action, detail in TOKEN_FLOW
    )
    return f"""
    <div class="intro page-break-after">
      <h1>API Test Scenarios</h1>
      <p class="subtitle">Complete Testing Reference — {count} Scenarios</p>
      <p class="meta">Banking Domain API Playground · Generated {date.today().isoformat()}</p>

      <h2>Getting Started</h2>
      <ol class="steps">
        <li>Start the platform: <code>make up-full</code></li>
        <li>Import Postman environment: <code>docs/postman/playground-environment.json</code></li>
        <li>Import Postman collection: <code>docs/postman/playground-api-collection.json</code></li>
        <li>Run login scenarios first (008–010) to obtain JWT tokens</li>
        <li>Execute scenarios in order or jump to critical scenarios below</li>
      </ol>

      <h2>Service Base URLs</h2>
      <table class="info-table">
        <tr><th>Service</th><th>Base URL</th><th>Auth Type</th></tr>
        <tr><td>Platform API</td><td>http://localhost:8080</td><td>Basic (learner / learner)</td></tr>
        <tr><td>Banking API</td><td>http://localhost:8081</td><td>JWT Bearer token</td></tr>
        <tr><td>Enterprise API</td><td>http://localhost:8082</td><td>JWT Bearer token</td></tr>
        <tr><td>Test Lab API</td><td>http://localhost:8083</td><td>Basic (learner / learner)</td></tr>
      </table>

      <h2>Postman Variables</h2>
      <table class="info-table">
        <tr><th>Variable</th><th>Purpose</th></tr>
        <tr><td>jwt_token</td><td>Customer JWT — set automatically after Scenario 008</td></tr>
        <tr><td>admin_jwt</td><td>Ops/Admin JWT — set after Scenario 009 or 010</td></tr>
        <tr><td>idempotency_key</td><td>Unique key for payment/transfer/card calls (auto-generated)</td></tr>
      </table>

      <h2>Token-Based API Calls (JWT Bearer)</h2>
      <p>Most Banking and Enterprise endpoints require a JWT obtained via login. This is the standard
      tokenised authentication flow for protected API calls.</p>
      <table class="info-table">
        <tr><th>Step</th><th>Action</th><th>Details</th></tr>
        {token_rows}
      </table>
      <p class="note">Every protected request must include both <code>Authorization: Bearer &lt;token&gt;</code>
      and <code>X-Tenant-Id: tenant-demo</code>. Mismatch between token tenant and header returns 403 (Scenario 135).</p>

      <h2>Critical &amp; Complex Scenarios</h2>
      <p>These scenarios cover security boundaries, financial integrity, idempotency, fraud detection,
      concurrency, and resilience. Prioritise them for regression and learning paths.</p>
      <table class="info-table critical-table">
        <tr><th>Scenarios</th><th>Topic</th><th>Why It Matters</th></tr>
        {critical_rows}
      </table>

      <h2>Scenario Categories</h2>
      <ul class="categories">
        <li><strong>001–007</strong> Platform API — health, version, error demos, validation</li>
        <li><strong>008–052</strong> Banking API — auth, parties, KYC, accounts, payments, transfers, cards, FX, ledger</li>
        <li><strong>053–098</strong> Enterprise API — loans, deposits, fraud, AML, admin, webhooks, resilience</li>
        <li><strong>099–133</strong> Test Lab — reset, scenarios, mocks, faults, performance, security profiles</li>
        <li><strong>134–138</strong> Negative &amp; edge cases — auth failures, tenant mismatch, idempotency conflict, insufficient funds</li>
      </ul>
    </div>
    """


def build_toc_html(index: list[tuple[str, str]]) -> str:
    groups: dict[str, list[tuple[str, str]]] = {
        "Platform": [],
        "Banking": [],
        "Enterprise": [],
        "Lab": [],
    }
    for num, name in index:
        prefix = name.split(" — ", 1)[0]
        groups.get(prefix, groups["Lab"]).append((num, name))

    parts = ['<div class="toc page-break-after"><h2>Scenario Index</h2>']
    for group, items in groups.items():
        if not items:
            continue
        parts.append(f'<h3>{html.escape(group)} API</h3><table class="toc-table">')
        for num, name in items:
            parts.append(
                f'<tr><td class="toc-num">{num}</td><td><a href="#scenario-{num}">{esc(name)}</a></td></tr>'
            )
        parts.append("</table>")
    parts.append("</div>")
    return "\n".join(parts)


def build_scenario_html(s: dict) -> str:
    header_rows = "".join(
        f"<tr><td>{esc(k)}</td><td><code>{esc(v)}</code></td></tr>" for k, v in s["headers"]
    )
    if not header_rows:
        header_rows = '<tr><td colspan="2" class="muted">No headers required</td></tr>'

    body_html = '<p class="muted">None</p>'
    if s["body"]:
        body_html = f'<pre class="json">{esc(s["body"])}</pre>'

    resp_html = '<p class="muted">None (empty body)</p>'
    if s["response"]:
        resp_html = f'<pre class="json">{esc(s["response"])}</pre>'

    notes_html = ""
    if s["notes"]:
        notes_html = f'<div class="notes"><strong>Note:</strong> {esc(s["notes"])}</div>'

    auth_class = ""
    if "JWT" in s["auth"] or "Bearer" in s["auth"]:
        auth_class = " auth-jwt"
    elif "Basic" in s["auth"]:
        auth_class = " auth-basic"
    elif s["auth"] == "None":
        auth_class = " auth-none"

    return f"""
    <div class="scenario page-break-inside" id="scenario-{s['num']}">
      <div class="scenario-header">
        <span class="scenario-num">Scenario {s['num']}</span>
        <h2>{esc(s['name'])}</h2>
      </div>
      <table class="meta-table">
        <tr><th>URL</th><td><code>{esc(s['url'])}</code></td></tr>
        <tr><th>Method</th><td><span class="method method-{esc(s['method'])}">{esc(s['method'])}</span></td></tr>
        <tr><th>Authentication</th><td><span class="auth-badge{auth_class}">{esc(s['auth'])}</span></td></tr>
        <tr><th>Expected Status</th><td><span class="status">{esc(s['status'])}</span></td></tr>
      </table>

      <h3>Request Headers</h3>
      <table class="headers-table">
        <tr><th>Header</th><th>Value</th></tr>
        {header_rows}
      </table>

      <h3>Request Body</h3>
      {body_html}

      <h3>Expected Response</h3>
      {resp_html}
      {notes_html}
    </div>
    """


CSS = """
@page {
  size: A4;
  margin: 18mm 16mm 20mm 16mm;
  @frame footer {
    -pdf-frame-content: footerContent;
    bottom: 8mm;
    margin-left: 16mm;
    margin-right: 16mm;
    height: 10mm;
  }
}
body {
  font-family: Helvetica, Arial, sans-serif;
  font-size: 9.5pt;
  color: #1a1a2e;
  line-height: 1.45;
}
h1 {
  font-size: 22pt;
  color: #0f3460;
  margin-bottom: 4pt;
  border-bottom: 3px solid #0f3460;
  padding-bottom: 8pt;
}
h2 {
  font-size: 13pt;
  color: #0f3460;
  margin-top: 14pt;
  margin-bottom: 6pt;
}
h3 {
  font-size: 10pt;
  color: #16213e;
  margin-top: 10pt;
  margin-bottom: 4pt;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.subtitle { font-size: 12pt; color: #533483; margin: 0; }
.meta { font-size: 8.5pt; color: #666; margin-bottom: 16pt; }
.intro { margin-bottom: 20pt; }
.steps { margin-left: 14pt; }
.steps li { margin-bottom: 4pt; }
.note {
  background: #eef4ff;
  border-left: 3px solid #0f3460;
  padding: 8pt 10pt;
  font-size: 9pt;
  margin-top: 8pt;
}
.categories li { margin-bottom: 3pt; }
code, pre {
  font-family: Courier, monospace;
  font-size: 8pt;
}
pre.json {
  background: #f4f6f8;
  border: 1px solid #dde3ea;
  border-radius: 4px;
  padding: 8pt;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.info-table, .meta-table, .headers-table, .toc-table {
  width: 100%;
  border-collapse: collapse;
  margin: 6pt 0 10pt 0;
  font-size: 8.5pt;
}
.info-table th, .meta-table th, .headers-table th, .toc-table th {
  background: #0f3460;
  color: white;
  text-align: left;
  padding: 5pt 8pt;
}
.info-table td, .meta-table td, .headers-table td, .toc-table td {
  border: 1px solid #dde3ea;
  padding: 4pt 8pt;
  vertical-align: top;
}
.meta-table th { width: 28%; background: #e8edf5; color: #0f3460; }
.critical-table td:first-child { white-space: nowrap; font-weight: bold; }
.toc-num { width: 36pt; font-weight: bold; color: #0f3460; }
.toc a { color: #0f3460; text-decoration: none; }
.scenario {
  border: 1px solid #dde3ea;
  border-radius: 6px;
  padding: 10pt 12pt;
  margin-bottom: 14pt;
  background: #fafbfc;
}
.scenario-header { margin-bottom: 8pt; }
.scenario-num {
  display: inline-block;
  background: #0f3460;
  color: white;
  font-size: 8pt;
  font-weight: bold;
  padding: 2pt 8pt;
  border-radius: 10px;
  margin-bottom: 4pt;
}
.scenario h2 {
  margin: 4pt 0 0 0;
  font-size: 11pt;
}
.method {
  font-weight: bold;
  padding: 1pt 6pt;
  border-radius: 3px;
  font-size: 8pt;
}
.method-GET { background: #d4edda; color: #155724; }
.method-POST { background: #cce5ff; color: #004085; }
.method-PATCH { background: #fff3cd; color: #856404; }
.method-PUT { background: #f8d7da; color: #721c24; }
.method-DELETE { background: #f5c6cb; color: #721c24; }
.auth-badge {
  font-size: 8pt;
  padding: 2pt 6pt;
  border-radius: 3px;
  background: #e9ecef;
}
.auth-jwt { background: #d1ecf1; color: #0c5460; }
.auth-basic { background: #fff3cd; color: #856404; }
.auth-none { background: #e2e3e5; color: #383d41; }
.status { font-weight: bold; color: #0f3460; }
.notes {
  background: #fff8e1;
  border-left: 3px solid #f9a825;
  padding: 6pt 8pt;
  margin-top: 8pt;
  font-size: 8.5pt;
}
.muted { color: #888; font-style: italic; }
.page-break-after { page-break-after: always; }
.page-break-inside { page-break-inside: avoid; }
#footerContent {
  font-size: 7.5pt;
  color: #999;
  text-align: center;
  border-top: 1px solid #eee;
  padding-top: 4pt;
}
"""


def build_html(content: str) -> str:
    count = scenario_count(content)
    index = parse_index(content)
    scenarios = parse_scenarios(content)
    intro = build_intro_html(count)
    toc = build_toc_html(index)
    body = "\n".join(build_scenario_html(s) for s in scenarios)
    return f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>API Test Scenarios — {count} Scenarios</title>
  <style>{CSS}</style>
</head>
<body>
  {intro}
  {toc}
  <div class="scenarios">
    <h1 class="page-break-after">Detailed Scenarios</h1>
    {body}
  </div>
  <div id="footerContent">API Testing Playground — Banking Domain · {count} scenarios</div>
</body>
</html>"""


def html_to_pdf(html_content: str, pdf_path: Path) -> None:
    with pdf_path.open("wb") as pdf_file:
        status = pisa.CreatePDF(html_content, dest=pdf_file, encoding="utf-8")
    if status.err:
        raise RuntimeError(f"PDF generation failed with {status.err} errors")


def build_guide_md(count: int, scenarios: list[dict]) -> str:
    lines = [
        f"# API Test Scenarios — Testing Guide ({count} scenarios)",
        "",
        "Clean, printable reference for API testing. Copy each scenario into Postman.",
        "",
        "## Getting Started",
        "",
        "1. Start the platform: `make up-full`",
        "2. Import Postman environment: `docs/postman/playground-environment.json`",
        "3. Import Postman collection: `docs/postman/playground-api-collection.json`",
        "4. Run login scenarios 008–010 first to obtain JWT tokens",
        "",
        "## Service Base URLs",
        "",
        "| Service | Base URL | Authentication |",
        "|---------|----------|----------------|",
        "| Platform API | http://localhost:8080 | Basic (learner / learner) |",
        "| Banking API | http://localhost:8081 | JWT Bearer token |",
        "| Enterprise API | http://localhost:8082 | JWT Bearer token |",
        "| Test Lab API | http://localhost:8083 | Basic (learner / learner) |",
        "",
        "## Postman Variables",
        "",
        "| Variable | Purpose |",
        "|----------|---------|",
        "| jwt_token | Customer JWT — set after Scenario 008 |",
        "| admin_jwt | Ops/Admin JWT — set after Scenario 009 or 010 |",
        "| idempotency_key | Unique key for payment, transfer, and card calls |",
        "",
        "## Token-Based API Calls (JWT Bearer)",
        "",
        "Most Banking and Enterprise endpoints require a JWT obtained via login.",
        "",
        "| Step | Action | Details |",
        "|------|--------|---------|",
    ]
    for step, action, detail in TOKEN_FLOW:
        lines.append(f"| {step} | {action} | {detail} |")
    lines.extend([
        "",
        "Every protected request must include `Authorization: Bearer <token>` and `X-Tenant-Id: tenant-demo`.",
        "",
        "## Critical and Complex Scenarios",
        "",
        "| Scenarios | Topic | Why It Matters |",
        "|-----------|-------|----------------|",
    ])
    for s, title, desc in CRITICAL_SCENARIOS:
        lines.append(f"| {s} | {title} | {desc} |")
    lines.extend(["", "---", "", "## Detailed Scenarios", ""])
    for s in scenarios:
        lines.extend([
            f"### Scenario {s['num']} — {s['name']}",
            "",
            f"URL: {s['url']}",
            f"Method: {s['method']}",
            f"Authentication: {s['auth']}",
            f"Expected Status: {s['status']}",
            "",
            "Request Headers:",
            "",
            "| Header | Value |",
            "|--------|-------|",
        ])
        if s["headers"]:
            for k, v in s["headers"]:
                lines.append(f"| {k} | {v} |")
        else:
            lines.append("| — | No headers required |")
        lines.extend(["", "Request Body:", ""])
        if s["body"]:
            lines.extend(["```json", s["body"], "```"])
        else:
            lines.append("None")
        lines.extend(["", "Expected Response:", ""])
        if s["response"]:
            lines.extend(["```json", s["response"], "```"])
        else:
            lines.append("None (empty body)")
        if s["notes"]:
            lines.extend(["", f"Note: {s['notes']}"])
        lines.extend(["", "---", ""])
    return "\n".join(lines)


def main() -> None:
    content = SOURCE_MD.read_text(encoding="utf-8")
    scenarios = parse_scenarios(content)
    count = scenario_count(content)
    html_content = build_html(content)
    OUTPUT_HTML.write_text(html_content, encoding="utf-8")
    OUTPUT_GUIDE_MD.write_text(build_guide_md(count, scenarios), encoding="utf-8")
    html_to_pdf(html_content, OUTPUT_PDF)
    print(f"Wrote {OUTPUT_GUIDE_MD}")
    print(f"Wrote {OUTPUT_HTML}")
    print(f"Wrote {OUTPUT_PDF} ({len(scenarios)} scenarios)")


if __name__ == "__main__":
    main()
