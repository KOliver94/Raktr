import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../_services/auth.service';
import {Router} from '@angular/router';

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
    {path: '/devices', title: 'Eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: true},
    {path: '/rents', title: 'Kivitelek kezelése', icon: 'local_shipping', class: '', inMenuBar: true},
    {path: '/tickets', title: 'Hibajegyek', icon: 'bug_report', class: '', inMenuBar: true},
    {path: '/user-profile', title: 'Beállítások', icon: 'person', class: '', inMenuBar: true},
];

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
    menuItems: any[];

    constructor(private authService: AuthService,
                private router: Router) {
    }

    ngOnInit() {
        this.menuItems = ROUTES.filter(menuItem => menuItem);
    }

    isMobileMenu() {
        return $(window).width() <= 991;
    };

    logout() {
        this.authService.logout();
        this.router.navigateByUrl('/login');
    }
}
