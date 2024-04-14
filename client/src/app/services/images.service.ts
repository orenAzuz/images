import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  constructor(private http: HttpClient) { }

  uploadFile(formData: any){
    return this.http.post('/api/addImage', formData,{ responseType: "text" as 'json' });
  }
  deleteImage(id: any) {
    return this.http.delete('/api/deleteImage/' + id,{ responseType: "text" as 'json' });
  }
  getAll() {
    return this.http.get('/api/images');
  }


}
