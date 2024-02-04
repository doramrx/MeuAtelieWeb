import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneFormatter',
  standalone: true,
})
export class PhonePipe implements PipeTransform {
  transform(value: string): string {

    const areaCode = value.substring(0, 2);
    const firstDigit = value.substring(2, 3);
    const centralPart = value.substring(3, 7);
    const finalPart = value.substring(7, 11);

    return `(${areaCode}) ${firstDigit} ${centralPart}-${finalPart}`;
  }
}
