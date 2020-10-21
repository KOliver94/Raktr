import {Component, OnInit} from '@angular/core';

declare const $: any;

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
    inMenuBar: boolean;
}

export const ROUTES: RouteInfo[] = [
    {path: '/overview', title: 'Áttekintés', icon: 'dashboard', class: '', inMenuBar: true},
    {path: '/devices', title: 'Eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: true}, //content_paste
    {path: '/rents', title: 'Bérlések kezelése', icon: 'content_paste', class: '', inMenuBar: true},
    {path: '/user-profile', title: 'Személyes beállítások', icon: 'person', class: '', inMenuBar: true},
];

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
    menuItems: any[];

    constructor() {
    }

    ngOnInit() {
        this.menuItems = ROUTES.filter(menuItem => menuItem);
    }

    isMobileMenu() {
        return $(window).width() <= 991;
    };
}
