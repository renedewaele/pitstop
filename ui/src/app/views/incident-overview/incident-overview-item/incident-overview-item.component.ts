import {Component, Input, OnDestroy} from '@angular/core';
import {View} from '../../../common/view';
import {HandleQuery} from '../../../common/handle-query';
import {filter, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Handler} from '../../../common/handler';
import {AppSettings, Incident, Offer} from '@pitstop/typescriptmodels/pitstop';
import {OfferModalComponent} from '../offer-modal/offer-modal.component';
import {AppContext} from '../../../app-context';
import moment from 'moment/moment';

@Component({
  selector: 'app-incident-overview-item',
  templateUrl: './incident-overview-item.component.html',
  styleUrls: ['./incident-overview-item.component.scss']
})
@Handler()
export class IncidentOverviewItemComponent extends View implements OnDestroy {
  @Input() incident: Incident;

  escalationPercentage: number;
  private escalationInterval;
  settings = this.sendQuery("/api/settings")
    .pipe(filter((s : AppSettings) => s && !!s.escalationDelay))
    .subscribe(s =>
      setInterval(this.escalationInterval = () => this.updateEscalationTimer(s), 100));

  updateEscalationTimer = (settings: AppSettings) => {
    const max = moment.duration(settings.escalationDelay).asMilliseconds();
    if (max) {
      const start = moment(this.incident.start);
      const now = moment();
      const duration = moment.duration(now.diff(start));
      const current = Math.max(0, duration.asMilliseconds());
      this.escalationPercentage = Math.min(current/max * 100, 100);
    }
  }

  ngOnDestroy(): void {
    if (this.escalationInterval) {
      clearInterval(this.escalationInterval);
    }
  }

  @HandleQuery("getIncident")
  getIncident(): Observable<Incident> {
    return this.subscribeTo("getIncidents").pipe(map(
      (incidents: Incident[]) => incidents.find(o => o.incidentId === this.incident.incidentId)));
  }

  addOffer(): void {
    this.openModal(OfferModalComponent, c => c.incidentId = this.incident.incidentId);
  }

  escalateIncident() {
    this.sendCommand(`/api/incidents/${this.incident.incidentId}/escalate`, {});
  }

  closeIncident() {
    this.sendCommand(`/api/incidents/${this.incident.incidentId}/close`, {});
  }

  trackByOfferId = (index: number, record: Offer) => record.offerId;

  protected readonly AppContext = AppContext;
}
