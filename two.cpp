
#include <iostream> 
#include <omp.h>
#include <random> 
#include <ctime> 
#include <chrono>
union Int32
{
    int x;
    unsigned char bytes[4];
};

union Int32 *list;//массив для параллельной версии
union Int32 *listS;//массив для последовательной версии
int Size;//размер обрабатываемого массива
int threads;//число потоков
int *Step;//массив номеров элементов, откуда начинать

void initialization(char** argv)  //инициализация 
{
    int i;
    Size = atoi(argv[1]);//размер массива берется из консоли

    list = new union Int32[Size];
    listS = new union Int32[Size];

    for (i = 0; i < Size; i++)
    {
        listS[i].x = list[i].x = rand() % 50 - 10;
    }

    threads = atoi(argv[2]);//число потоков берется из консоли
    Step = new int[threads + 1];

    for (i = 0; i < threads; i++)
    {
        Step[i] = i * Size / threads + (i * Size / threads) % 2;
    }
    Step[threads] = Size;
}

// Сортируем целые числа без знака по n-байту.
static void radix_sort_byte(union Int32* arr, int left, int right, int nByte)
{

    union Int32 *p = new union Int32[right - left];
    //257 , т.к ещё есть знак "минус"
    int count[257] = { 0 }, i;

    for (i = left; i < right; i++)
        count[arr[i].bytes[nByte] + 1]++;

    for (i = 1; i < 256; i++)
        count[i] += count[i - 1];

    for (i = left; i < right; i++)
        p[count[arr[i].bytes[nByte]]++] = arr[i];

    for (i = left; i < right; i++)
        arr[i] = p[i - left];
    delete[] p;

}

// Сортировка целых чисел со знаком: переключаем знаковый бит, сортируем как беззнаковые, возвращаем знаковый бит.
static void radix_sort(union Int32* arr, int left, int right)
{
    int i;
    unsigned *x = (unsigned*)arr;

    for (i = left; i < right; i++)
        x[i] ^= INT_MIN;

    // Сортируем числа от младшего байта к старшему.
    for (i = 0; i < 4; i++)
        radix_sort_byte(arr, left, right, i);

    for (i = left; i < right; i++)
        x[i] ^= INT_MIN;
}

void  Splitter(int sign, union Int32* mas, union Int32* tmp, int size1, int size2)
{
    for (int i = sign; i < size1; i += 2)
        tmp[i] = mas[i];

    union Int32* mas2 = mas + size1;

    int a = sign;
    int b = sign;
    int i = sign;

    while ((a < size1) && (b < size2))
    {
        if (tmp[a].x <= mas2[b].x)
        {
            mas[i] = tmp[a];
            a += 2;
        }
        else
        {
            mas[i] = mas2[b];
            b += 2;
        }

        i += 2;
    }

    if (a == size1)
        for (int j = b; j < size2; j += 2, i += 2)
            mas[i] = mas2[j];
    else
        for (int j = a; j < size1; j += 2, i += 2)
            mas[i] = tmp[j];
}

//выполняется сравнение чётных и нечётных пар элементов массива, проходя по массиву один раз.
void SimpleComparator(union Int32* mas, int size)
{
    for (int i = 1; i < (size + 1) / 2; i++)
        if (mas[2 * i].x < mas[2 * i - 1].x)
        {
            int tmp = mas[2 * i - 1].x;
            mas[2 * i - 1] = mas[2 * i];
            mas[2 * i].x = tmp;
        }
}


void sort(union Int32* list, int size, int * step, int threads) //сборка частей
                                                 //s - число потоков
{

    union Int32* tmp = new union Int32[size + 1];
    int tid;
    int level = threads;
    int *restep = new int[threads + 1];//вспомогательные шаги

    for (int i = 0; i < threads; i++)
    {
        step[i] = i * size / threads + (i * size / threads) % 2;
    }
    step[threads] = size;


    while ((level != 1))
    {
        // выделение отсортированных четно-нечетных подпоследовательностей
#pragma omp parallel for private(tid) 
        for (tid = 0; tid < level; tid++)
        {
            int t = tid % 2;
            Splitter(t, list + step[tid - t], tmp + step[tid], step[tid + (1 - t)] - step[tid - t], step[tid + 1 + (1 - t)] - step[tid + (1 - t)]);
        }
#pragma omp parallel for private(tid) 
        for (tid = 0; tid < level; tid++)
        {
            if (tid % 2 != 0)
            {
                SimpleComparator(list + step[tid - 1], step[tid + 1] - step[tid - 1]);
            }
        }

        level = level / 2;

        restep = new int[level];
        restep[0] = step[0];
        for (int i = 1; i < level; i++)
        {

            restep[i] = step[i * 2];

        }

        step = new int[level];
        for (int i = 0; i < level; i++)
        {
            step[i] = restep[i];
        }
        step[level] = size;

        //std::cout << "\n \n";
        //for (int j = 0; j < level + 1; j++)
        //{
        //    std::cout << "=>" << step[j];
        //}


    }
#pragma omp barrier 

    delete[] restep;
    delete[] tmp;


}
bool isAscending(union Int32 arr[], int size) {
    for (int i = 0; i < size - 1; i++) {
        if (arr[i].x > arr[i + 1].x) {
            return false;
        }
    }
    return true;
}
//int main(int argc, char **argv)
//{
//    initialization(argv);
//    //Последовательная сортировка
//    omp_set_num_threads(1);
//
//    double start_timeS = omp_get_wtime();
//
//    radix_sort(listS, 0, Size);
// 
//    double end_timeS = omp_get_wtime();
//
//    std::cout << "\n\n Time Serial Radix: \n" << end_timeS - start_timeS;
//
//    //Параллельная сортировка
//    omp_set_num_threads(threads);
//
//    double start_time = omp_get_wtime();
//
//    int i;
//#pragma omp parallel for private(i) //каждый поток отсортирует свою часть
//    for (i = 0; i < threads; i++)
//    {
//        radix_sort(list, Step[i], Step[i + 1]);
//    }
//    double end_time;
//    if (threads > 1)
//    {
//        sort(list, Size, Step,threads);
//    }
//
//    end_time = omp_get_wtime();
//    std::cout << "\n\n Time Parallel Radix with Batcher: \n" << end_time - start_time;
//
//    std::cout << "\n\n Jump: \n" << (end_timeS - start_timeS) / (end_time - start_time);
//    if (isAscending(list, Size))
//    {
//        std::cout << "\n CORRECT";
//    }
//    else
//    {
//        std::cout << "\n FALSE";
//    }
//    return 0;
//}
int main(int argc, char **argv)
{

    int i;

    initialization(argv);

    if (Size < 33)
    {
        std::cout << "\n" << " Input: " << "\n";
        for (int i = 0; i < Size; i++)
        {
            std::cout << list[i].x << "|";
        }
    }
    std::cout << "\n" << " Steps:" << "\n";
    for (int j = 0; j < threads + 1; j++)
    {
        std::cout << "=>" << Step[j];
    }

    //Последовательная сортировка
    omp_set_num_threads(1);

    double start_timeS = omp_get_wtime();

    for (int i = 0; i < threads; i++)
    {
        radix_sort(listS, Step[i], Step[i + 1]);
    }
    if (Size < 33)
    {
        std::cout << "\n\n" << " After Serial Radix: " << "\n";
        for (int i = 0; i < Size; i++)
        {
            std::cout << listS[i].x << "|";
        }
    }
    sort(listS, Size, Step, threads);

    if (Size < 33)
    {
        std::cout << "\n\n" << " After Serial Batcher : " << "\n";
        for (int i = 0; i < Size; i++)
        {
            std::cout << listS[i].x << "|";
        }
    }
    double end_timeS = omp_get_wtime();

    std::cout << "\n\n Time Serial Radix with Batcher: \n" << end_timeS - start_timeS;

    //Параллельная сортировка
    omp_set_num_threads(threads);

    double start_time = omp_get_wtime();

#pragma omp parallel for private(i) //каждый поток отсортирует свою часть
    for (i = 0; i < threads; i++)
    {
        radix_sort(list, Step[i], Step[i + 1]);
    }

    if (Size < 33)
    {
        std::cout << "\n\n" << " After Parallel Radix: " << "\n";
        for (int i = 0; i < Size; i++)
        {
            std::cout << list[i].x << "|";
        }
    }
    std::cout << "\n\n";
    //Параллельное слияние
    double end_time;
    if (threads > 1)
    {
        sort(list, Size, Step, threads);

        if (Size < 33)
        {
            std::cout << " After Parallel Batcher :" << "\n";
            for (int i = 0; i < Size; i++)
            {
                std::cout << list[i].x << "|";
            }

        }

        end_time = omp_get_wtime();
        std::cout << "\n\n Time Parallel Radix with Batcher: \n" << end_time - start_time;

        std::cout << "\n\n Jump: \n" << (end_timeS - start_timeS) / (end_time - start_time);
        if (isAscending(list, Size))
        {
            std::cout << "\n CORRECT";
        }
        else
        {
            std::cout << "\n FALSE";
        }
    }
    delete[] list;
    delete[] listS;
}

