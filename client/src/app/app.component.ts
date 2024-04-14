import { Component } from '@angular/core';
import { ImagesService } from './services/images.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  base64Image: any;
  imageRegex = /^image\/(jpeg|png|gif|bmp|webp)/;
  images:any[] = [];
  success:boolean = false;
  message:any;
  constructor(
    private service:ImagesService,
    private domSanitizer: DomSanitizer,
  ) { 
   this.getData(); 
  }
  getData(){
    this.service.getAll().subscribe((data: any)=>{this.images = data;console.log(data);});
    this.endMessage();
  }
  onFileSelected(event:any): void {
    // const file = input.files?.item(0);
    const file: File = event.target.files[0];
    if (!file) {
      console.error('No file selected');
      return;
    }
    console.log(file.type);
    
    if(!this.imageRegex.test(file.type)){
      this.success = false;
      this.message = 'Only image type file is allowed';
      this.endMessage();
      return;
    }
    this.fileToBase64(file).then(base64 =>{
      this.base64Image = this.domSanitizer.bypassSecurityTrustUrl('data:'+ file.type + ';base64,'+base64 );
      const fileToupload = {
        name:file.name,
        file:base64,
        type:file.type
      }
      this.service.uploadFile(fileToupload).subscribe(data => {
          this.success = true;
          this.message = data;
          this.getData();
      },error=>{
        this.success = false;
        this.message = error.error;
        this.endMessage();
      });
    });

  }
  endMessage(){
    setTimeout(()=>{this.message = null},3500);
  }
  fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result as string;
        const base64Data = base64String.split(',')[1]; // Extract only the base64 portion
        resolve(base64Data);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
  deleteImage(id:any){
    this.service.deleteImage(id).subscribe(data => {
      this.success = true;
      this.message = data;
      this.getData();
    },error=>{
      this.success = false;
      this.message = error.error;
      this.endMessage();
    });
  }
}
