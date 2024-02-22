import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'costFormatter',
  standalone: true,
})
export class CostPipe implements PipeTransform {

  transform(value: number): string {
    console.log(value);
    const formattedValue = value.toFixed(2);
    return formattedValue;
  }
}
