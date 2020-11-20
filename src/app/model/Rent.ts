import {RentItem} from './RentItem';

export class Rent {
    id: number;
    destination: string;
    renter: string;
    issuer: string;
    outDate: string;
    expBackDate: string;
    actBackDate: string;
    rentItems: RentItem[];

    static toJsonString(rent: Rent): string {
        return `{\"Rent\": ${JSON.stringify(rent)}}`;
    }

    static fromJson(rent: Rent): Rent {
        const newRent = new Rent();
        newRent.id = rent.id;
        newRent.destination = rent.destination;
        newRent.issuer = rent.issuer;
        newRent.renter = rent.renter;
        newRent.outDate = rent.outDate;
        newRent.expBackDate = rent.expBackDate;
        newRent.actBackDate = rent.actBackDate;
        newRent.rentItems = [];

        rent.rentItems.forEach(rentItem => newRent.rentItems.push(RentItem.fromJson(rentItem)));

        return newRent;
    }

    constructor(id: number = -1, destination: string = '', renter: string = '', issuer: string = '',
                outDate: string = '', expBackDate: string = '', actBackDate: string = '', rentItems: RentItem[] = []) {
        this.id = id;
        this.destination = destination;
        this.renter = renter;
        this.issuer = issuer;
        this.outDate = outDate;
        this.expBackDate = expBackDate;
        this.actBackDate = actBackDate;
        this.rentItems = rentItems;
    }

    getSumWeight(): number {
        let sumWeight = 0;
        this.rentItems.forEach(rentItem => sumWeight += rentItem.scannable.getWeight());

        return sumWeight;
    }
}
