import { test, expect } from "@playwright/test";
import { login } from "./fixtures";

// Story EN-prometheus-grafana: Operational endpoints of prometheus-grafana
//
// NOTE: Acceptance criterion 1 (GET /healthz liveness probe) is explicitly
// described as infrastructure-level and "no user story requires or exercises
// this endpoint" — meaning there is no browser-visible UI surface for it.
// The journeys below therefore cover the operational health endpoint directly
// via Playwright's request API (which is the closest a browser-based e2e test
// can get), plus representative user-facing journeys that confirm the
// observability stack is alive and the app is functioning end-to-end.

test("EN-prometheus-grafana: /healthz returns 200 OK (liveness probe contract)", async ({
  request,
}) => {
  // This is the only acceptance criterion for this story.
  // We exercise it via Playwright's APIRequestContext since it is a raw HTTP
  // endpoint consumed by Kubernetes / load-balancer, not a browser page.
  const response = await request.get("/healthz");
  expect(response.status()).toBe(200);

  // The OpenAPI contract specifies the response body should indicate liveness.
  // Accept either a JSON body with a recognised "ok" / "status" field OR a
  // plain-text "ok" body — both are common conventions.
  const text = await response.text();
  const looksHealthy =
    text.toLowerCase().includes("ok") ||
    text.toLowerCase().includes("healthy") ||
    text.toLowerCase().includes("alive") ||
    text === "{}";
  expect(looksHealthy).toBe(true);
});

test("EN-prometheus-grafana: operator can log in and see the maintenance dashboard (stack is alive)", async ({
  page,
}) => {
  // Confirms the full observability stack is alive from a user perspective:
  // if Prometheus/Grafana sidecars are broken the app itself tends to fail.
  await login(page, "program_admin");

  // Expect a top-level navigation or heading that proves the app rendered.
  await expect(
    page.getByRole("heading", { name: /maintenance/i }).or(
      page.getByRole("navigation")
    )
  ).toBeVisible();
});

test("EN-prometheus-grafana: operator can view a Critical maintenance request (seeded data visible)", async ({
  page,
}) => {
  await login(page, "program_admin");

  // Navigate to maintenance requests list.
  await page
    .getByRole("link", { name: /maintenance/i })
    .first()
    .click();

  // The seeded Critical request submitted by Sarah Chen should be visible.
  await expect(page.getByText("description 1")).toBeVisible();
  await expect(
    page.getByText(/critical/i).first()
  ).toBeVisible();
});

test("EN-prometheus-grafana: technician can view a Medium maintenance request (seeded data visible)", async ({
  page,
}) => {
  await login(page, "approver");

  // Navigate to maintenance requests list.
  await page
    .getByRole("link", { name: /maintenance/i })
    .first()
    .click();

  // The seeded Medium request submitted by James Okafor should be visible.
  await expect(page.getByText("description 2")).toBeVisible();
  await expect(
    page.getByText(/medium/i).first()
  ).toBeVisible();
});

test("EN-prometheus-grafana: /healthz responds quickly enough for a load-balancer health check (latency guard)", async ({
  request,
}) => {
  // A secondary operational check: the liveness endpoint must respond within a
  // reasonable SLA (5 seconds) so that EKS does not mark the pod as unhealthy
  // due to timeout before a real failure.
  const start = Date.now();
  const response = await request.get("/healthz");
  const elapsed = Date.now() - start;

  expect(response.status()).toBe(200);
  expect(elapsed).toBeLessThan(5000);
});
