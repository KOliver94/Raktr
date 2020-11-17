import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Scannable} from '../model/Scannable';
import {map} from 'rxjs/operators';
import {Device} from '../model/Device';
import {CompositeItem} from '../model/CompositeItem';

@Injectable({
    providedIn: 'root'
})
export class ScannableService {

    constructor(private http: HttpClient) {
    }

    // @ts-ignore
    getScannableByBarcode(barcode: string): Observable<Scannable> {
        // @ts-ignore
        return this.http.get(`${environment.apiUrl}/api/scannable/${barcode}`, {observe: 'response'})
            .pipe(
                map(res => {
                    if (res.status === 404) {
                        return undefined;
                    } else {
                        if (res.body['@type'] === 'device') {
                            return res.body as Device;
                        } else if (res.body['@type'] === 'compositeItem') {
                            return res.body as CompositeItem;
                        }
                    }
                })
            )
    }

}
