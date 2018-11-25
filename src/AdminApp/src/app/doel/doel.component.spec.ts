import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DoelComponent } from './doel.component';

describe('DoelComponent', () => {
  let component: DoelComponent;
  let fixture: ComponentFixture<DoelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DoelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DoelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
