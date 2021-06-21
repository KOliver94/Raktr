import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Category} from '../model/Category';
import {Location} from '../model/Location';
import {switchMap, tap} from 'rxjs/operators';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Device} from '../model/Device';
import {LocationService} from '../services/location.service';
import {CategoryService} from '../services/category.service';
import {DeviceService} from '../services/device.service';
import {UserService} from '../services/user.service';
import {MatDialog} from '@angular/material/dialog';
import {User} from '../model/User';
import * as $ from 'jquery';
import {ScannableService} from '../services/scannable.service';
import {barcodeValidator} from '../helpers/barcode.validator';
import {textIdValidator} from '../helpers/textId.validator';
import {DeviceStatus} from '../model/DeviceStatus';

@Component({
    selector: 'app-edit-device-modal',
    templateUrl: './edit-device-modal.component.html',
    styleUrls: ['./edit-device-modal.component.css']
})
export class EditDeviceModalComponent implements OnInit {
    @Input() title: string;
    @Input() device: Device;

    categoryOptions: Category[];
    filteredCategoryOptions: Category[];
    locationOptions: Location[];
    filteredLocationOptions: Location[];
    deviceForm: FormGroup;
    admin = false;
    deleteConfirmed = false;

    currentCategoryInput = '';
    currentLocationInput = '';

    constructor(public activeModal: NgbActiveModal,
                private fb: FormBuilder,
                private locationService: LocationService,
                private categoryService: CategoryService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private userService: UserService,
                public dialog: MatDialog) {
        if (this.device === undefined) {
            this.device = new Device();
            this.device.id = -1;
        }
    }

    ngOnInit(): void {
        this.deviceForm = this.fb.group({
            name: ['', Validators.required],
            isPublicRentable: [''],
            maker: [''],
            type: [''],
            category: ['', Validators.required],
            location: ['', Validators.required],
            barcode: ['', Validators.required, barcodeValidator(this.scannableService, this.device.id)],
            textIdentifier: ['', Validators.required, textIdValidator(this.scannableService, this.device.id)],
            weight: ['0'],
            value: ['0'],
            quantity: ['1']
        });

        if (this.device.id === null || this.device.id === -1) {
            this.scannableService.getNextId().subscribe(nextid => {
                // @ts-ignore
                this.device.barcode = nextid.toString().padStart(7, '0');

                this.setFormFields();
            })
        } else {
            this.setFormFields();
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.admin = User.isFullAccessMember(user);
        });

        this.deviceForm
            .get('category')
            .valueChanges
            .pipe(
                tap(value => this.currentCategoryInput = value),
                switchMap(value => this.categoryService.getCategories())
            )
            .subscribe(categories => {
                this.categoryOptions = categories;
                this.filteredCategoryOptions = this._filterCategories(categories, this.currentCategoryInput);
            });

        this.deviceForm
            .get('location')
            .valueChanges
            .pipe(
                tap(value => this.currentLocationInput = value),
                switchMap(value => this.locationService.getLocations())
            )
            .subscribe(locations => {
                this.locationOptions = locations;
                this.filteredLocationOptions = this._filterLocations(locations, this.currentLocationInput);
            });

        this.deviceForm.get('barcode').markAsTouched();
        this.deviceForm.get('textIdentifier').markAsTouched();
    }

    private setFormFields() {
        this.deviceForm.setValue({
            name: this.device.name,
            isPublicRentable: this.device.isPublicRentable,
            maker: this.device.maker,
            type: this.device.type,
            category: this.device.category === null ? '' : this.device.category.name,
            location: this.device.location === null ? '' : this.device.location.name,
            barcode: this.device.barcode,
            textIdentifier: this.device.textIdentifier,
            weight: this.device.weight,
            value: this.device.value,
            quantity: this.device.quantity
        });
    }

    private _filterCategories(categories: Category[], value: string): Category[] {
        const filterValue = value.toLowerCase();

        return categories.filter(category => category.name.toLowerCase().includes(filterValue));
    }

    private _filterLocations(locations: Location[], value: string): Location[] {
        const filterValue = value.toLowerCase();

        return locations.filter(location => location.name.toLowerCase().includes(filterValue));
    }

    save() {
        const values = this.deviceForm.value;
        this.device.name = values.name.toString();
        this.device.isPublicRentable = values.isPublicRentable;
        this.device.maker = values.maker.toString();
        this.device.type = values.type.toString();
        this.device.category = new Category(-1, values.category.toString());
        this.device.location = new Location(-1, values.location.toString());
        this.device.barcode = values.barcode.toString();
        this.device.textIdentifier = values.textIdentifier.toString();
        this.device.weight = values.weight;
        this.device.value = values.value;
        this.device.quantity = values.quantity;

        if (this.device.id === -1) {
            // new
            this.deviceService.addDevice(this.device).subscribe(
                (device) => {
                    this.device = device;
                    this.activeModal.dismiss(device)
                },
                (error) => {
                    this.device.id = -1;
                    this.showNotification('Nem sikerült menteni, ütközés!', 'warning');
                });
        } else {
            // update
            this.deviceService.updateDevice(this.device as Device).subscribe(
                (device) => {
                    this.device = device;
                    this.activeModal.dismiss(device);
                },
                (error) => {
                    this.showNotification('Nem sikerült menteni, ütközés!', 'warning');
                })
        }
    }

    onStatusChange(value: string) {
        switch (value) {
            case '0':
                this.device.status = DeviceStatus.GOOD;
                break;
            case '1':
                this.device.status = DeviceStatus.NEEDS_REPAIR;
                break;
            case '2':
                this.device.status = DeviceStatus.SCRAPPED;
                break;
        }
    }

    delete(device: Device) {
        this.deviceService.deleteDevice(device).subscribe(device_ => {
            this.activeModal.dismiss('delete')
        });
    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 1000,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }
}
