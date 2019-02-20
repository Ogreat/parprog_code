
#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <iostream>
#include <vector>
union Int32
{
    int x;
    unsigned char bytes[4];
};


union Int32 *list;        //массив для параллельной версии
union Int32 *listS;       //массив для последовательной версии
int size;//размер обрабатываемого массива

int threads;//число потоков

int *step;//массив номеров элементов, откуда начинать

void initialization(char** argv)  //инициализация 
{
    int i;
    size = atoi(argv[1]);//размер массива берется из консоли

    list = (union Int32 *)malloc(size * sizeof(union Int32));
    listS = (union Int32 *)malloc(size * sizeof(union Int32));
    for (i = 0; i < size; i++)
    {
        listS[i].x = list[i].x = rand() % 500000 - 100000;
    }

    threads = atoi(argv[2]);//число потоков берется из консоли

    step = (int *)malloc((threads + 1) * sizeof(int));

    for (i = 0; i < threads; i++)
    {
        step[i] = i * size / threads;
    }
    step[threads] = size;

}

//четно-нечетное слияние Бэтчера 
void exchange(union Int32* list, int i, int j)
{
    int t = list[i].x;
    list[i].x = list[j].x;
    list[j].x = t;
}
void compare(union Int32* list, int i, int j)
{
    if (list[i].x > list[j].x)
        exchange(list, i, j);
}
//lo - начальная позиция 
//n - длина части, подлежащей объединению,
//r - расстояние элементов, подлежащих сравнению
void oddEvenMerge(union Int32* list, int lo, int n, int r)
{
    int m = r * 2;
    if (m < n)
    {

#pragma omp parallel sections
        {

#pragma omp section 
            {
                oddEvenMerge(list, lo, n, m); // четная подпоследовательность
            }
#pragma omp section 
            {
                oddEvenMerge(list, lo + r, n, m);// нечетная 
            }
        }
        int k = lo + n;
        int g = k - r;

        for (int i = lo + r; i < g; i += m)
        {
            compare(list, i, i + r);
        }
    }
    else
    {
        compare(list, lo, lo + r);
    }
}
//сортирует кусок длины n массива
//начиная с позиции lo

void oddEvenMergeSort(union Int32* list, int lo, int n)
{
    if (n > 1)
    {
        oddEvenMerge(list, lo, n, 1);//объединяем 

    }
}
void sortB(union Int32* list, int Size)
{
    oddEvenMergeSort(list, 0, Size);
}


//сортировка слиянием
void merge(union Int32* A, union Int32* B, int size1, int size2)//слияние
{
    int i = 0, j = 0, k = 0, t;
    int size = size1 + size2;
    int *temp = (int *)malloc(size * sizeof(int));//буфер для слияния


    while (i < size1 && j < size2) {
        if (A[i].x <= B[j].x)
        {
            temp[k] = A[i++].x;
        }
        else { temp[k] = B[j++].x; }
        k++;
    }

    if (i < size1) {
        for (t = i; t < size1; t++, k++)   // скопировать остальные элементы отсортированного массива A в массив temp
            temp[k] = A[t].x;
    }
    else {
        for (t = j; t < size2; t++, k++)
            temp[k] = B[t].x;
    }

    for (i = 0; i < size; i++)
        A[i].x = temp[i];

    free(temp);
}
void sort(union Int32* list, int size, int *step, int s) //сборка частей
                                                 //s - число потоков
{
    int i;
    while ((s > 1) && (s != 2))
    {
        for (i = 0; i < s; i++)
        {
            step[i] = i * size / s;
        }
        step[s] = size;

        //параллельное слияние
#pragma omp parallel for private(i) 
        for (i = 0; i < s; i = i + 2)
        {
            merge(list + step[i], list + step[i + 1], step[i + 1] - step[i], step[i + 2] - step[i + 1]);
        }
        s = s / 2;
    }
}

#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


// Сортируем целые числа без знака по n-байту.
static void radix_sort_byte(union Int32* arr,int left, int right, int nByte)
{
    union Int32 *p;
    p = (union Int32*)malloc((right-left) * sizeof(union Int32));
    //257 , т.к ещё есть знак "минус"
    int count[257] = { 0 }, i;

    for (i = left; i < right; i++)
        count[arr[i].bytes[nByte] + 1]++;

    for (i = 1; i < 256; i++)
        count[i] += count[i - 1];

    for (i = left; i < right; i++)
        p[count[arr[i].bytes[nByte]]++] = arr[i];

    for (i = left; i < right; i++)
        arr[i] = p[i-left];

    free(p);
}

// Сортировка целых чисел со знаком: переключаем знаковый бит, сортируем как беззнаковые, возвращаем знаковый бит.
static void radix_sort(union Int32* arr,int left,int right)
{
    int i;
   unsigned *x = (unsigned*)arr;

    for (i = left; i < right; i++)
        x[i] ^= INT_MIN;

    // Сортируем числа от младшего байта к старшему.
    for (i = 0; i < 4; i++)
        radix_sort_byte(arr,left,right, i);

    for (i = left; i < right; i++)
        x[i] ^= INT_MIN;
}

int main(int argc, char **argv)
{

    int i;

    initialization(argv);

    if ((size != 0) && ((size & (size - 1)) == 0))
    {
        if (size < 33)
        {
            std::cout << "\n" << " Input: " << "\n";
            for (int i = 0; i < size; i++)
            {
                std::cout << list[i].x << "|";
            }
        }
        std::cout << "\n" << " Steps:" << "\n";
        for (int j = 0; j < threads+1; j++)
        {
            std::cout << "=>" << step[j];
        }

        //Последовательная сортировка
        omp_set_num_threads(1);

        double start_timeS = omp_get_wtime();
       
        for (int i = 0; i < threads; i++)
        {
            radix_sort(listS, step[i], step[i + 1]);
        }
        if (size < 33)
        {
            std::cout << "\n\n" << " After Serial Radix: " << "\n";
            for (int i = 0; i < size; i++)
            {
                std::cout << listS[i].x << "|";
            }
        }
        sort(listS, size, step, threads);
        sortB(listS, size);//четно-нечетное слияние Бэтчера для отсортированных половинок
        if (size < 33)
        {
            std::cout << "\n\n" << " After Serial Batcher : " << "\n";
            for (int i = 0; i < size; i++)
            {
                std::cout << listS[i].x << "|";
            }
        }
        double end_timeS = omp_get_wtime();
        free(listS);
        std::cout << "\n\n Time Serial Radix with Batcher: \n" << end_timeS - start_timeS;


        //Параллельная сортировка со слиянием
        omp_set_num_threads(threads);


        double start_time = omp_get_wtime();

#pragma omp parallel for private(i) //каждый поток отсортирует свою часть
        for (i = 0; i < threads; i++)
        {
            radix_sort(list, step[i], step[i+1]);          
        }

        if (size < 33)
        {
            std::cout << "\n\n" << " After Parallel Radix: " << "\n";
            for (int i = 0; i < size; i++)
            {
                std::cout << list[i].x << "|";
            }
        }
        std::cout << "\n\n";
        //Параллельное слияние
        double end_time;
        if (threads > 1)
        {
        
            sort(list, size, step, threads);
            sortB(list, size);//четно-нечетное слияние Бэтчера для отсортированных половинок
            if (size < 33)
            {
                std::cout <<  " After Parallel Batcher :" << "\n";
                for (int i = 0; i < size; i++)
                {
                    std::cout << list[i].x << "|";
                }
            }
            end_time = omp_get_wtime();
            std::cout << "\n\n Time Parallel Radix with Batcher: \n" << end_time - start_time;
        }

       free(list);
       std::cout << "\n\n Jump: \n" << (end_timeS - start_timeS) / (end_time - start_time);
    }

}




//тесты:
//16777216
//8388608
//4194304
//2097152
