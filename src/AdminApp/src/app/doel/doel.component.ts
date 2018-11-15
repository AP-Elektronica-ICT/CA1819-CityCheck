import { Component, OnInit } from '@angular/core';
import { latLng, LatLng, tileLayer } from 'leaflet';

@Component({
  selector: 'app-doel',
  templateUrl: './doel.component.html',
  styleUrls: ['./doel.component.scss']
})
export class DoelComponent implements OnInit {


  constructor() { }

  ngOnInit() {


  }


  options = {
    layers: [
      tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'City Check Locs' })
    ],
    zoom: 10,
    center: latLng(46.879966, -50.726909)
  };


}
