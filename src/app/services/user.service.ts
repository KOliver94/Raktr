import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../model/User';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class UserService {


    constructor(private http: HttpClient) {
    }

    public getUser(username: string): Observable<User> {
        return this.http.get<User>(`${environment.apiUrl}/api/user/${username}`);
    }

    public updateUser(user: User): Observable<User> {
        const body = `{\"User\": ${JSON.stringify(user)}}`;
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        console.log(body);
        return this.http.put<User>(`${environment.apiUrl}/api/user/`, body, {headers: headers});
    }

    public getCurrentUser(): Observable<User> {
        return this.getUser(localStorage.getItem('username'))
    }
}
