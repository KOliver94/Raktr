import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {RouterModule} from '@angular/router';

import {AppRoutingModule} from './app.routing';
import {ComponentsModule} from './components/components.module';

import {AppComponent} from './app.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {RentsComponent} from './rents/rents.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {EditDeviceModalComponent} from './edit-device-modal/edit-device-modal.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EditRentComponent} from './edit-rent/edit-rent.component';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSuffix} from '@angular/material/form-field';
import {LoginComponent} from './login/login.component';

@NgModule({
    imports: [
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        ComponentsModule,
        RouterModule,
        AppRoutingModule,
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatCheckboxModule,
        MatAutocompleteModule,
        NgbModule,
        MatButtonModule,
        MatIconModule,
        MatToolbarModule
    ],
    declarations: [
        AppComponent,
        AdminLayoutComponent,
        RentsComponent,
        EditDeviceModalComponent,
        EditRentComponent,
        LoginComponent,
    ],
    providers: [
        {provide: MAT_DATE_LOCALE, useValue: 'hu-HU'},
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
