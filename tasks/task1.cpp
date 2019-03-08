﻿// Copyright 2019 Lalykin Oleg

#include <omp.h>
#include <iostream>
#include <string>
#include <cstdint>
#include <limits>

class Task {
 public:
    union Int32 {
        int x;
        unsigned char bytes[4];
    };
    union Int32 *list;
    int *Step;
    int Size;
    int threads;

    void initialization(char** argv) {
        int i;
        Size = atoi(argv[1]);

        list = new union Int32[Size];

        for (i = 0; i < Size; i++) {
            list[i].x = std::rand() % 50 - 10;
        }

        threads = atoi(argv[2]);
        Step = new int[threads + 1];

        for (i = 0; i < threads; i++) {
            Step[i] = i * Size / threads + (i * Size / threads) % 2;
        }
        Step[threads] = Size;
    }

    void Destructor() {
        delete[] Step;
        delete[] list;
    }

    void  SortSubsequence(int sign, union Int32* arr, union Int32* tmp, int left, int right) {
        for (int i = sign; i < left; i += 2) {
            tmp[i] = arr[i];//копирую часть, соответствующей кратности
        }
        union Int32* rightArr = arr + left;//начало второго отсортированного массива
        //признак кратности
        int a = sign;
        int b = sign;
        int i = sign;
        //пока не врежусь в барьер одного из массивов
        while ((a < left) && (b < right)) {//записываю в очередной меньший элемент
            if (tmp[a].x <= rightArr[b].x) {
                arr[i] = tmp[a];
                a += 2;//двигаю итератор массива, откуда взяли меньший элемент
            } else {
                arr[i] = rightArr[b];
                b += 2;
            }
            i += 2;//сдвиг общего итератора для массива куда записываю
        }
        if (a == left) {//если врезались в левый массив
            for (int j = b; j < right; j += 2, i += 2) {//записать остальные элементы правого массива 
                //(с соблюдением шага кратности)
                arr[i] = rightArr[j];
            }
        } else {//иначе так же с правым массивом
            for (int j = a; j < left; j += 2, i += 2) {//запись из левого массива
                arr[i] = tmp[j];
            }
        }
    }

    void PairComparison(union Int32* arr, int size) {
        //проходя по массиву один раз, попарная смена пар элементов местами, чтобы было по возрастанию
        for (int i = 1; i < (size + 1) / 2; i++) {
            if (arr[2 * i].x < arr[2 * i - 1].x) {
                int tmp = arr[2 * i - 1].x;
                arr[2 * i - 1] = arr[2 * i];
                arr[2 * i].x = tmp;
            }
        }
    }

    void merge(union Int32* list, int size, int * step, int threads) {
        union Int32* tmp = new union Int32[size + 1];
        int i;
        int level = threads;
        int *restep = new int[threads + 1];
        for (int i = 0; i < threads; i++) {
            step[i] = i * size / threads + (i * size / threads) % 2;
        }
        step[threads] = size;
        while ((level != 1)) {
            for (i = 0; i < level; i++) {
                int t = i % 2;
                SortSubsequence(t, list + step[i - t], tmp + step[i],
                    step[i + (1 - t)] - step[i - t], step[i + 1 + (1 - t)] - step[i + (1 - t)]);
            }
            for (i = 0; i < level; i++) {
                if (i % 2 != 0) {
                    PairComparison(list + step[i - 1], step[i + 1] - step[i - 1]);
                }
            }
            level = level / 2;
            restep = new int[level];
            restep[0] = step[0];
            for (int i = 1; i < level; i++) {
                restep[i] = step[i * 2];
            }
            step = new int[level];
            for (int i = 0; i < level; i++) {
                step[i] = restep[i];
            }
            step[level] = size;
        }
    }

    void Check(union Int32* arr, int size) {
        bool f = true;
        for (int i = 0; i < size - 1; i++) {
            if (arr[i].x > arr[i + 1].x) {
                f = false;
            }
        }
        if (f) {
            std::cout << "\n\n CORRECT \n";
        } else {
            std::cout << "\n\n FALSE \n";
        }
    }

    void PrintList(std::string str, union Int32* arr, int size) {
        if (size < 33) {
            std::cout << "\n\n" << str << "\n";
            for (int i = 0; i < size; i++) {
                std::cout << arr[i].x << "|";
            }
        }
    }

    void PrintSteps(int * step, int threads) {
        std::cout << "\n\n" << " Steps:" << "\n";
        for (int j = 0; j < threads + 1; j++) {
            std::cout << "=>" << step[j];
        }
    }

    void radix_sort_byte(union Int32* arr, int left, int right, int numByte, const int base) {
        //base == 256
        union Int32 *tmp = new union Int32[right - left];//вспомогательный массив
        int* count = new int[base];//массив подсчётов
        for (int i = 0; i < base + 2; i++) {//инициализация массива подсчётов
            count[i] = 0;
        }
        for (int i = left; i < right; i++) {//подсчёт numByte-го байта в каждом числе
            count[arr[i].bytes[numByte] + 1]++; // arr[i].bytes[numByte] принимает значения [0;255]
            //позиции в count : [0;256]
        }
        //идем по каждому разряду
        for (int i = 1; i < base; i++) {//подсчет индексов по которым будут сохраняться элементы
            count[i] += count[i - 1];
        }
        for (int i = left; i < right; i++) {//копирование элемента во вспомогательный массив по
            //соответствующему индексу в массиве подсчетов, выполняется инкремент индекса
            tmp[count[arr[i].bytes[numByte]]++] = arr[i];
        }
        for (int i = left; i < right; i++) {//изменение исходного массива arr , где tmp - отсортированная часть исходного
            arr[i] = tmp[i - left];
        }
    }

    void radix_sort(union Int32* arr, int left, int right) {
        int i;
        unsigned *subArr = reinterpret_cast<unsigned*>(arr);//переопределение как беззнаковый
        for (i = left; i < right; i++) {//применение исключающего ИЛИ на INT_MIN ,
            //что даст нужный временный вид последовательности
            subArr[i] = subArr[i] ^ std::numeric_limits<std::int32_t>::min();
        }
 //преобразовав временно последовательность, её можно сортировать как положительную
        for (i = 0; i < 4; i++) {//двигаясь от 1 до 4 байта
            radix_sort_byte(arr, left, right, i, 256);//последовательная сортировка чисел по разрядам (от
        //младшего разряда к старшему), считая что массив беззнаковый
        }
// возвращение (снова применение исключающего ИЛИ на INT_MIN)
        for (i = left; i < right; i++) {
            subArr[i] = subArr[i] ^ std::numeric_limits<std::int32_t>::min();
        }
    }

    void RunTask(char **argv) {
        initialization(argv);
        PrintList(" Input: ", list, Size);
        PrintSteps(Step, threads);
        for (int i = 0; i < threads; i++) {
            radix_sort(list, Step[i], Step[i + 1]);
        }
        PrintList(" After radix: ", list, Size);
        if (threads > 1) {
            merge(list, Size, Step, threads);
        }
        PrintList(" After merge: ", list, Size);
        Check(list, Size);
        Destructor();
    }

    void RunDefault() {
        int i;
        Size = 30;

        list = new union Int32[Size];

        for (i = 0; i < Size; i++) {
            list[i].x = std::rand() % 50 - 10;
        }

        threads = 4;
        Step = new int[threads + 1];

        for (i = 0; i < threads; i++) {
            Step[i] = i * Size / threads + (i * Size / threads) % 2;
        }
        Step[threads] = Size;
        PrintList(" Input: ", list, Size);
        PrintSteps(Step, threads);
        for (int i = 0; i < threads; i++) {
            radix_sort(list, Step[i], Step[i + 1]);
        }
        PrintList(" After radix: ", list, Size);
        if (threads > 1) {
            merge(list, Size, Step, threads);
        }
        PrintList(" After merge: ", list, Size);
        Check(list, Size);
        Destructor();
    }
};
int main(int argc, char **argv) {
    Task t;
    if (argc == 3) {
        t.RunTask(argv);
    } else {
        t.RunDefault();
    }
    return 0;
}