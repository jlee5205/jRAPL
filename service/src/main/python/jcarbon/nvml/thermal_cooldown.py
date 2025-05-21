import os

from argparse import ArgumentParser

import pandas as pd
from time import sleep, time, time_ns

from pynvml import nvmlInit, nvmlDeviceGetCount
from pynvml import nvmlDeviceGetHandleByIndex, nvmlDeviceGetTemperature, NVML_TEMPERATURE_GPU

def parse_args():
    """ Parses client-side arguments. """
    parser = ArgumentParser()
    parser.add_argument(
        '-p',
        '--period',
        dest='period',
        type=float,
        default=10,
        help='period to sample from the nvml',
    )
    parser.add_argument(
        '-t',
        '--temperature',
        dest='temperature',
        type=float,
        default=45,
        help='the target temperature in celsius',
    )
    parser.add_argument(
        '--output_path',
        default=f'/tmp/jcarbon-{os.getpid()}.csv'
    )
    return parser.parse_args()

def cooldown(period, temperature, output_path):
    k = 10
    p = 0.80 * k

    nvmlInit()
    gpu_count = nvmlDeviceGetCount()

    samples = {i: [] for i in range(gpu_count)}
    timestamps = []
    max_len = 0

    start = time()
    while(True):
        ts = time_ns()
        temps = {}
        met = {}
        sample_count = 0
        for i in range(gpu_count):
            handle = nvmlDeviceGetHandleByIndex(i)
            temp = nvmlDeviceGetTemperature(handle, NVML_TEMPERATURE_GPU)

            s = samples[i]
            s.append(temp)
            if(len(s) > k):
                s.pop(0)
            
            sample_count = len(s)

            temps[i] = sum(s) / len(s)
            met[i] = len(s) == k and sum(temp < temperature for temp in s) >= p
            timestamps.append({'ts': ts, 'temp': temp})

        zone_status = [f'{i}->{temps[i]} C ({met[i]})' for i in range(gpu_count)]
        status = ','.join(zone_status)

        message = f'zone status ({sample_count}/{k}): {status}'
        max_len = max(max_len, len(message))
        print('\r{message: <{fill}}'.format(message=message, fill=max_len), flush=True)
        if all(met.values()):
            break
        sleep(period)

    elapsed = time() - start
    print(f'cooled down to {temperature} C in {elapsed}')

    timestamps_output = os.path.join(os.path.dirname(output_path), 'temperature_timestamps.csv')
    print(f'writing jcarbon batch timestamps report to {timestamps_output}')
    pd.DataFrame(timestamps).to_csv(timestamps_output)

def main():
    args = parse_args()
    cooldown(args.period, args.temperature, args.output_path)

if __name__ == '__main__':
    main()