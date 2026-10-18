import { TestBed } from '@angular/core/testing';

import { TertiaryVerifyService } from './tertiary-verify.service';

describe('TertiaryVerifyService', () => {
  let service: TertiaryVerifyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TertiaryVerifyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
